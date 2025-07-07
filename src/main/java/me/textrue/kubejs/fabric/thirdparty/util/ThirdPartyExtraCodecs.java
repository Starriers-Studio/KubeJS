package me.textrue.kubejs.fabric.thirdparty.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class ThirdPartyExtraCodecs {
	public static final StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> REGISTRY_FRIENDLY_BYTE_BUF = new StreamCodec<>() {

		@Override
		public RegistryFriendlyByteBuf decode(RegistryFriendlyByteBuf buf) {
			RegistryFriendlyByteBuf newBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), buf.registryAccess());
			newBuf.writeBytes(buf.copy());
			buf.skipBytes(buf.readableBytes());
			return newBuf;
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, RegistryFriendlyByteBuf toEncode) {
			buf.writeBytes(toEncode.copy());
			toEncode.release();
		}
	};

	public static <T> MapCodec<T> aliasedFieldOf(final Codec<T> codec, final String... names) {
		if (names.length == 0)
			throw new IllegalArgumentException("Must have at least one name!");
		MapCodec<T> mapCodec = codec.fieldOf(names[0]);
		for (int i = 1; i < names.length; i++)
			mapCodec = mapWithAlternative(mapCodec, codec.fieldOf(names[i]));
		return mapCodec;
	}

	/**
	 * Similar to {@link Codec#optionalFieldOf(String, Object)}, except that the default value is always written.
	 */
	public static <T> MapCodec<T> optionalFieldAlwaysWrite(Codec<T> codec, String name, T defaultValue) {
		return codec.optionalFieldOf(name).xmap(o -> o.orElse(defaultValue), Optional::of);
	}

	public static <T> MapCodec<T> mapWithAlternative(final MapCodec<T> mapCodec, final MapCodec<? extends T> alternative) {
		return Codec.mapEither(mapCodec, alternative).xmap(either -> either.map(Function.identity(), Function.identity()), Either::left);
	}

	/**
	 * Creates a codec for a list from a codec of optional elements.
	 * The empty optionals are removed from the list when decoding.
	 */
	public static <A> Codec<List<A>> listWithOptionalElements(Codec<Optional<A>> elementCodec) {
		return listWithoutEmpty(elementCodec.listOf());
	}

	/**
	 * Creates a codec for a list of optional elements, that removes empty values when decoding.
	 */
	public static <A> Codec<List<A>> listWithoutEmpty(Codec<List<Optional<A>>> codec) {
		return codec.xmap(
			list -> list.stream().filter(Optional::isPresent).map(Optional::get).toList(),
			list -> list.stream().map(Optional::of).toList());
	}

	/**
	 * Map dispatch codec with an alternative.
	 *
	 * <p>The alternative will only be used if there is no {@code "type"} key in the serialized object.
	 *
	 * @param typeCodec     codec for the dispatch type
	 * @param type          function to retrieve the dispatch type from the dispatched type
	 * @param codec         function to retrieve the dispatched type map codec from the dispatch type
	 * @param fallbackCodec fallback to use when the deserialized object does not have a {@code "type"} key
	 * @param <A>           dispatch type
	 * @param <E>           dispatched type
	 * @param <B>           fallback type
	 */
	public static <A, E, B> MapCodec<Either<E, B>> dispatchMapOrElse(Codec<A> typeCodec, Function<? super E, ? extends A> type, Function<? super A, ? extends MapCodec<? extends E>> codec, MapCodec<B> fallbackCodec) {
		var dispatchCodec = typeCodec.dispatchMap(type, codec);
		return new MapCodec<>() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.concat(dispatchCodec.keys(ops), fallbackCodec.keys(ops)).distinct();
			}

			@Override
			public <T> DataResult<Either<E, B>> decode(DynamicOps<T> ops, MapLike<T> input) {
				if (input.get("type") != null) {
					return dispatchCodec.decode(ops, input).map(Either::left);
				} else {
					return fallbackCodec.decode(ops, input).map(Either::right);
				}
			}

			@Override
			public <T> RecordBuilder<T> encode(Either<E, B> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				return input.map(
					dispatched -> dispatchCodec.encode(dispatched, ops, prefix),
					fallback -> fallbackCodec.encode(fallback, ops, prefix));
			}

			@Override
			public String toString() {
				return "DispatchOrElse[" + dispatchCodec + ", " + fallbackCodec + "]";
			}
		};
	}

	/**
	 * Codec that matches exactly one out of two map codecs.
	 * Same as {@link Codec#xor} but for {@link MapCodec}s.
	 */
	public static <F, S> MapCodec<Either<F, S>> xor(MapCodec<F> first, MapCodec<S> second) {
		return new XorMapCodec<>(first, second);
	}

	private static final class XorMapCodec<F, S> extends MapCodec<Either<F, S>> {
		private final MapCodec<F> first;
		private final MapCodec<S> second;

		private XorMapCodec(MapCodec<F> first, MapCodec<S> second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.concat(first.keys(ops), second.keys(ops)).distinct();
		}

		@Override
		public <T> DataResult<Either<F, S>> decode(DynamicOps<T> ops, MapLike<T> input) {
			DataResult<Either<F, S>> firstResult = first.decode(ops, input).map(Either::left);
			DataResult<Either<F, S>> secondResult = second.decode(ops, input).map(Either::right);
			var firstValue = firstResult.result();
			var secondValue = secondResult.result();
			if (firstValue.isPresent() && secondValue.isPresent()) {
				return DataResult.error(
					() -> "Both alternatives read successfully, cannot pick the correct one; first: " + firstValue.get() + " second: "
						+ secondValue.get(),
					firstValue.get());
			} else if (firstValue.isPresent()) {
				return firstResult;
			} else if (secondValue.isPresent()) {
				return secondResult;
			} else {
				return firstResult.apply2((x, y) -> y, secondResult);
			}
		}

		@Override
		public <T> RecordBuilder<T> encode(Either<F, S> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			return input.map(x -> first.encode(x, ops, prefix), x -> second.encode(x, ops, prefix));
		}

		@Override
		public String toString() {
			return "XorMapCodec[" + first + ", " + second + "]";
		}
	}
}
