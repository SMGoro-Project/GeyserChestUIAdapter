package re.imc.geyserchestuiadapter;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.Location;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.data.packets.OpenWindow;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.InstanceHolder;
import org.slf4j.Logger;

@Plugin(
        id = "geyserchestuiadapter",
        name = "GeyserChestUIAdapter",
        version = "1.0-SNAPSHOT",
        authors = {"zimzaza4"},
        dependencies = {
                @Dependency(id = "protocolize")
        }
)
public class GeyserChestUIAdapter {

    public static GsonComponentSerializer SERIALIZER = GsonComponentSerializer.builder().downsampleColors().build();
    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        Protocolize.listenerProvider().registerListener(new AbstractPacketListener<OpenWindow>(OpenWindow.class, Direction.UPSTREAM, 0) {
            public void packetReceive(PacketReceiveEvent<OpenWindow> packetReceiveEvent) {
                
            }

            public void packetSend(PacketSendEvent<OpenWindow> packetSendEvent) {
                if (InstanceHolder.getApi().isFloodgatePlayer(packetSendEvent.player().uniqueId())) {
                    String appendTitle = null;
                    OpenWindow packet = packetSendEvent.packet();
                    switch (packet.inventoryType()) {
                        case GENERIC_9X1 -> appendTitle = "chest.row.1";
                        case GENERIC_9X2 -> appendTitle = "chest.row.2";
                        case GENERIC_9X4 -> appendTitle = "chest.row.4";
                        case GENERIC_9X5 -> appendTitle = "chest.row.5";
                    }

                    Component title = SERIALIZER.deserialize(packet.titleJson());
                    packet.titleJson(SERIALIZER.serialize(title.append(Component.text(appendTitle))));
                    packetSendEvent.packet(packet);
                }

            }
        });
    }
}
