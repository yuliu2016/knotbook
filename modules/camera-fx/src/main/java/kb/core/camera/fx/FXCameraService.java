package kb.core.camera.fx;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;

public class FXCameraService implements Service {

    private static ServiceMetadata metadata = new ServiceMetadata("JavaFX WebCam", "1.0");

    @Override
    public void launch(ServiceContext context) {
        context.getUIManager().registerCommand("camera.fx.test",
                "Test FX Camera",
                "mdi-camera",
                new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN),
                FXCameraTest::test
        );
    }

    @Override
    public ServiceMetadata getMetadata() {
        return metadata;
    }
}
