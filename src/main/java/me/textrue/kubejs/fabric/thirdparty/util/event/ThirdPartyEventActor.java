package me.textrue.kubejs.fabric.thirdparty.util.event;

@FunctionalInterface
public interface ThirdPartyEventActor<T> {
	ThirdPartyEventResult act(T t);
}
