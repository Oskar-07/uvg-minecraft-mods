package gt.edu.uvg.universityguide.client;

import gt.edu.uvg.universityguide.client.screen.ArrivalScreen;
import gt.edu.uvg.universityguide.client.screen.DestinationScreen;
import gt.edu.uvg.universityguide.client.screen.StopEditorScreen;
import gt.edu.uvg.universityguide.network.TourPayloads;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Opens server-requested screens on the client thread using immutable packet snapshots. */
public final class ClientPayloadHandler {
    private ClientPayloadHandler() {
    }

    public static void openDestinations(TourPayloads.OpenDestinations payload, IPayloadContext context) {
        Minecraft.getInstance().setScreen(new DestinationScreen(payload));
    }

    public static void openStopEditor(TourPayloads.OpenStopEditor payload, IPayloadContext context) {
        Minecraft.getInstance().setScreen(new StopEditorScreen(payload));
    }

    public static void showArrival(TourPayloads.ShowArrival payload, IPayloadContext context) {
        Minecraft.getInstance().setScreen(new ArrivalScreen(payload.title(), payload.description()));
    }
}
