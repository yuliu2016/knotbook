package kb.plugin.connectfour;

import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;

import javax.swing.*;

public class CNGService implements Service {

    private static ServiceMetadata metadata = new ServiceMetadata("Connect Four Game", "1.0");

    @Override
    public void launch(ServiceContext context) {
        context.getUIManager().registerCommand("connect4.start",
                "Start Connect 4 Game",
                "mdi-record",
                () -> SwingUtilities.invokeLater(ConnectFour::start)
        );
    }

    @Override
    public ServiceMetadata getMetadata() {
        return metadata;
    }
}
