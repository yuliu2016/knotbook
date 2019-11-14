package kb.tool.cng;

import kb.service.api.Service;
import kb.service.api.ServiceContext;
import kb.service.api.ServiceMetadata;

import javax.swing.*;

public class CNGService implements Service {

    private static ServiceMetadata metadata = new ServiceMetadata();

    static {
        metadata.setPackageName("kb.tool.cng");
        metadata.setPackageVersion("1.0");
    }

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
