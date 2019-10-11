package kb.application;

import kb.service.api.MetaService;
import kb.service.api.Service;
import kb.service.api.ServiceMetadata;
import kb.service.api.TextEditorService;
import kb.service.api.application.ApplicationService;
import kb.service.api.application.JVMInstance;
import kb.service.api.application.PrivilagedContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

@SuppressWarnings("unused")
public class KnotBook {

    private static <T extends MetaService> List<T> loadServices(Class<T> service) {
        List<T> providers = new ArrayList<>();
        for (T provider : ServiceLoader.load(service)) {
            providers.add(provider);
        }
        return providers;
    }

    private static <T extends MetaService> void print(List<T> services) {
        System.out.println("\nListing " + services.size() + " package(s):");
        for (T s : services) {
            ServiceMetadata metadata = s.getMetadata();
            System.out.println(metadata.getPackageName() + " => " + metadata.getPackageVersion());
        }
    }

    // application
    private static final List<ApplicationService> apps = loadServices(ApplicationService.class);

    // All extensions
    private static final List<Service> extensions = loadServices(Service.class);

    // Text Editor implementation
    private static final List<TextEditorService> textEditors = loadServices(TextEditorService.class);

    // App Registry
    private static final Registry registry = new Registry(new UserFile());

    static {
        print(apps);
        print(extensions);
        print(textEditors);
    }

    // App Context
    private static final PrivilagedContext context = new AppContextImpl(
            extensions,
            textEditors.get(0),
            registry
    );

    private static void launch() {
        System.out.println(Arrays.toString(JVMInstance.getArgs()));

        for (ApplicationService app : apps) {
            app.launchFast();
            app.launch(context);
        }

        for (Service service : extensions) {
            service.launch(new ServiceContextImpl(service, context));
        }

        /*

          Task has not declared any outputs despite executing actions.
Starting process 'command 'C:\Users\Yu\AppData\Local\OpenJDK\jdk-11.0.4+11\bin\java.exe''. Working directory: C:\Users\Yu\IdeaProjects\knotbook\modules\kb.application Command: C:\Users\Yu\AppData\Local\OpenJDK\jdk-11.0.4+11\bin\java.exe --module-path C:\Users\Yu\.gradle\caches\modules-2\files-2.1\com.nativelibs4java\bridj\0.7.0\461c40ed578c92106579e370838ed4e224d0289e\bridj-0.7.0.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\com.google.zxing\core\3.4.0\5264296c46634347890ec9250bc65f14b7362bf8\core-3.4.0.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.core.splash\libs\kb.core.splash.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.openjfx\javafx-base\13\43c52e1d11b38514e9d2421ad98ca6a35de12b0\javafx-base-13.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.core.bowline\libs\kb.core.bowline.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.openjfx\javafx-graphics\13\a9407212df2b75d557a509ec14a9e8e282494b4e\javafx-graphics-13.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.openjfx\javafx-base\13\6386f02a2b71d8b503c6ca3cd2d2dcb382ce86b4\javafx-base-13-win.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\com.fifesoft\rsyntaxtextarea\3.0.3\692470d5f32c1a9b139fe925b7db70fb081f227\rsyntaxtextarea-3.0.3.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.application\classes\merged;C:\Users\Yu\IdeaProjects\knotbook\build\kb.core.server\libs\kb.core.server.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.tba.client\libs\kb.tba.client.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-test\1.3.50\92a66b17cc20735cfb1438512210b643813f7516\kotlin-test-1.3.50.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.service.api\libs\kb.service.api.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.jetbrains\annotations\13.0\919f0dfe192fb4e063e7dacadee7f8bb9a2672a9\annotations-13.0.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.core.camera.fx\libs\kb.core.camera.fx.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib\1.3.50\b529d1738c7e98bbfa36a4134039528f2ce78ebf\kotlin-stdlib-1.3.50.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib-common\1.3.50\3d9cd3e1bc7b92e95f43d45be3bfbcf38e36ab87\kotlin-stdlib-common-1.3.50.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.kordamp.ikonli\ikonli-core\11.3.4\8a9954362d60c4b69d2b60b0a3874e7ce0038f0b\ikonli-core-11.3.4.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.kordamp.ikonli\ikonli-materialdesign-pack\11.3.4\8d1d01ac2af7ef5f9ffc882f94dbbe6a42af054b\ikonli-materialdesign-pack-11.3.4.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\com.github.sarxos\webcam-capture\0.3.12\47b09a421def93b23397bc900ca9a0a31a4927f1\webcam-capture-0.3.12.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.tool.cng\libs\kb.tool.cng.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.tool.path.planner\libs\kb.tool.path.planner.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.core.fx\libs\kb.core.fx.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.core.view\libs\kb.core.view.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\com.beust\klaxon\5.0.13\c477974bcd163d82b7061effc851db25082c0295\klaxon-5.0.13.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.slf4j\slf4j-api\1.7.6\562424e36df3d2327e8e9301a76027fca17d54ea\slf4j-api-1.7.6.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-test-common\1.3.50\667fd8bfc2db0bd546f3b90fe15fe83648437bea\kotlin-test-common-1.3.50.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.core.icon\libs\kb.core.icon.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\com.github.Team865\FRC-Commons-Kotlin\e00ce2ccc6\69a879b179b76fcfe80c272688eebc86ce77b60b\FRC-Commons-Kotlin-e00ce2ccc6.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.slf4j\slf4j-simple\1.7.6\12aaccff7811a8c619bad5954b96823589d2476e\slf4j-simple-1.7.6.jar;C:\Users\Yu\IdeaProjects\knotbook\build\kb.application\resources\main;C:\Users\Yu\IdeaProjects\knotbook\build\kb.core.code\libs\kb.core.code.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-reflect\1.3.50\b499f22fd7c3e9c2e5b6c4005221fa47fc7f9a7a\kotlin-reflect-1.3.50.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.openjfx\javafx-controls\13\7524fd4ab57de51637adb0e99f0220a1ae0ae4dd\javafx-controls-13-win.jar;C:\Users\Yu\.gradle\caches\modules-2\files-2.1\org.openjfx\javafx-graphics\13\bbcbc616702a086567585183efcb0ab8eb22a66b\javafx-graphics-13-win.jar --patch-module kb.application=C:\Users\Yu\IdeaProjects\knotbook\build\kb.application\resources\main --module kb.application/kb.application.KnotBook -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en -Duser.variant kb.application/kb.application.KnotBook

        */
    }

    public static void main(String[] args) {
        JVMInstance.setArgs(args);
        launch();
    }
}
