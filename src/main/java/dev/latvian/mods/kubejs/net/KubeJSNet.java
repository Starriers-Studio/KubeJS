package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import me.textrue.kubejs.fabric.helper.NetworkHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface KubeJSNet {
	private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String id) {
		return new CustomPacketPayload.Type<>(KubeJS.id(id));
	}

	CustomPacketPayload.Type<WebServerUpdateJSONPayload> WEB_SERVER_JSON_UPDATE = type("web_server_json_update");
	CustomPacketPayload.Type<WebServerUpdateNBTPayload> WEB_SERVER_NBT_UPDATE = type("web_server_nbt_update");
	CustomPacketPayload.Type<SendDataFromClientPayload> SEND_DATA_FROM_CLIENT = type("send_data_from_client");
	CustomPacketPayload.Type<SendDataFromServerPayload> SEND_DATA_FROM_SERVER = type("send_data_from_server");
	CustomPacketPayload.Type<AddStagePayload> ADD_STAGE = type("add_stage");
	CustomPacketPayload.Type<RemoveStagePayload> REMOVE_STAGE = type("remove_stage");
	CustomPacketPayload.Type<SyncStagesPayload> SYNC_STAGES = type("sync_stages");
	CustomPacketPayload.Type<FirstClickPayload> FIRST_CLICK = type("first_click");
	CustomPacketPayload.Type<NotificationPayload> NOTIFICATION = type("toast");
	CustomPacketPayload.Type<ReloadStartupScriptsPayload> RELOAD_STARTUP_SCRIPTS = type("reload_startup_scripts");
	CustomPacketPayload.Type<DisplayServerErrorsPayload> DISPLAY_SERVER_ERRORS = type("display_server_errors");
	CustomPacketPayload.Type<DisplayClientErrorsPayload> DISPLAY_CLIENT_ERRORS = type("display_client_errors");
	CustomPacketPayload.Type<SyncServerDataPayload> SYNC_SERVER_DATA = type("sync_server_data");
	CustomPacketPayload.Type<SetActivePostShaderPayload> SET_ACTIVE_POST_SHADER = type("set_active_post_shader");

	interface Kubedex {
		CustomPacketPayload.Type<RequestInventoryKubedexPayload> REQUEST_INVENTORY = type("kubedex/request_inventory");
		CustomPacketPayload.Type<RequestBlockKubedexPayload> REQUEST_BLOCK = type("kubedex/request_block");
		CustomPacketPayload.Type<RequestEntityKubedexPayload> REQUEST_ENTITY = type("kubedex/request_entity");
	}

	static void register() {
		NetworkHelper.playToClient(WEB_SERVER_JSON_UPDATE, WebServerUpdateJSONPayload.STREAM_CODEC, WebServerUpdateJSONPayload::handle);
		NetworkHelper.playToClient(WEB_SERVER_NBT_UPDATE, WebServerUpdateNBTPayload.STREAM_CODEC, WebServerUpdateNBTPayload::handle);
		NetworkHelper.playToServer(SEND_DATA_FROM_CLIENT, SendDataFromClientPayload.STREAM_CODEC, SendDataFromClientPayload::handle);
		NetworkHelper.playToClient(SEND_DATA_FROM_SERVER, SendDataFromServerPayload.STREAM_CODEC, SendDataFromServerPayload::handle);
		NetworkHelper.playToClient(ADD_STAGE, AddStagePayload.STREAM_CODEC, AddStagePayload::handle);
		NetworkHelper.playToClient(REMOVE_STAGE, RemoveStagePayload.STREAM_CODEC, RemoveStagePayload::handle);
		NetworkHelper.playToClient(SYNC_STAGES, SyncStagesPayload.STREAM_CODEC, SyncStagesPayload::handle);
		NetworkHelper.playToServer(FIRST_CLICK, FirstClickPayload.STREAM_CODEC, FirstClickPayload::handle);
		NetworkHelper.playToClient(NOTIFICATION, NotificationPayload.STREAM_CODEC, NotificationPayload::handle);
		NetworkHelper.playToClient(RELOAD_STARTUP_SCRIPTS, ReloadStartupScriptsPayload.STREAM_CODEC, ReloadStartupScriptsPayload::handle);
		NetworkHelper.playToClient(DISPLAY_SERVER_ERRORS, DisplayServerErrorsPayload.STREAM_CODEC, DisplayServerErrorsPayload::handle);
		NetworkHelper.playToClient(DISPLAY_CLIENT_ERRORS, DisplayClientErrorsPayload.STREAM_CODEC, DisplayClientErrorsPayload::handle);
		NetworkHelper.playToClient(SYNC_SERVER_DATA, SyncServerDataPayload.STREAM_CODEC, SyncServerDataPayload::handle);
		NetworkHelper.playToClient(SET_ACTIVE_POST_SHADER, SetActivePostShaderPayload.STREAM_CODEC, SetActivePostShaderPayload::handle);

		NetworkHelper.playToServer(Kubedex.REQUEST_INVENTORY, RequestInventoryKubedexPayload.STREAM_CODEC, RequestInventoryKubedexPayload::handle);
		NetworkHelper.playToServer(Kubedex.REQUEST_BLOCK, RequestBlockKubedexPayload.STREAM_CODEC, RequestBlockKubedexPayload::handle);
		NetworkHelper.playToServer(Kubedex.REQUEST_ENTITY, RequestEntityKubedexPayload.STREAM_CODEC, RequestEntityKubedexPayload::handle);
	}
}