package test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.ejb.AppResCommonIF;
import test.ejb.AppResRemoteIF;
import test.ejb.ServiceLocator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@ExtendWith(ArquillianExtension.class)
public class ClientTest {
    @Deployment(name = "two_standalone_component_web", order = 1, testable = false)
    public static JavaArchive createCommonDeployment() {
        JavaArchive two_standalone_component_ejb = ShrinkWrap.create(JavaArchive.class, "two_standalone_component_ejb");
        two_standalone_component_ejb.addClasses(
                test.ejb.AppResBeanBase.class,
                test.ejb.AppResCommonIF.class,
                test.ejb.AppResRemoteIF.class,
                test.ejb.Helper.class,
                test.ejb.ServiceLocator.class,
                test.ejb.Module2Bean.class
        );
        URL ejbResURL = ClientMain.class.getResource("/two_standalone_component_ejb.xml");
        two_standalone_component_ejb.addAsManifestResource(ejbResURL, "ejb-jar.xml");

        return two_standalone_component_ejb;
    }

    @Deployment(name = "ejb3_misc_moduleName_twojars", order = 2)
    public static EnterpriseArchive createDeployment() throws IOException {
        // Client
        // the jar with the correct archive name
        JavaArchive ejb3_misc_moduleName_twojars_client = ShrinkWrap.create(JavaArchive.class, "ejb3_misc_moduleName_twojars_client.jar");
        ejb3_misc_moduleName_twojars_client.addClasses(ClientMain.class, AppResCommonIF.class, AppResRemoteIF.class, ServiceLocator.class);
        // The application-client.xml descriptor
        URL resURL = ClientMain.class.getResource("/ejb3_misc_moduleName_twojars_client.xml");
        if(resURL != null) {
            //ejb3_misc_moduleName_twojars_client.addAsManifestResource(resURL, "application-client.xml");
        }
        // The sun-application-client.xml file need to be added or should this be in in the vendor Arquillian extension?
        resURL = ClientMain.class.getResource("/ejb3_misc_moduleName_twojars_client.jar.sun-application-client.xml");
        if(resURL != null) {
            //ejb3_misc_moduleName_twojars_client.addAsManifestResource(resURL, "sun-application-client.xml");
        }
        ejb3_misc_moduleName_twojars_client.addAsManifestResource(new StringAsset("Main-Class: " + ClientMain.class.getName() + "\n"), "MANIFEST.MF");

        // EJB
        JavaArchive ejb3_misc_moduleName_twojars_ejb = ShrinkWrap.create(JavaArchive.class, "ejb3_misc_moduleName_twojars_ejb.jar");
        // The class files
        ejb3_misc_moduleName_twojars_ejb.addClasses(
                test.ejb.ModuleBean.class
        );
        // The ejb-jar.xml descriptor
        URL ejbResURL = ClientMain.class.getResource("/ejb3_misc_moduleName_twojars_ejb.xml");
        if(ejbResURL != null) {
            //ejb3_misc_moduleName_twojars_ejb.addAsManifestResource(ejbResURL, "ejb-jar.xml");
        }
        // The sun-ejb-jar.xml file
        ejbResURL = ClientMain.class.getResource("/ejb3_misc_moduleName_twojars_ejb.jar.sun-ejb-jar.xml");
        if(ejbResURL != null) {
            ejb3_misc_moduleName_twojars_ejb.addAsManifestResource(ejbResURL, "sun-ejb-jar.xml");
        }
        JavaArchive shared_lib = ShrinkWrap.create(JavaArchive.class, "shared.jar");
        // The class files
        shared_lib.addClasses(
                test.ejb.AppResBeanBase.class,
                test.ejb.Helper.class,
                test.ejb.AppResCommonIF.class,
                test.ejb.AppResRemoteIF.class,
                test.ejb.ServiceLocator.class
        );

        // EAR
        EnterpriseArchive ejb3_misc_moduleName_twojars_ear = ShrinkWrap.create(EnterpriseArchive.class, "ejb3_misc_moduleName_twojars.ear");
        ejb3_misc_moduleName_twojars_ear.addAsLibrary(shared_lib);
        ejb3_misc_moduleName_twojars_ear.addAsModule(ejb3_misc_moduleName_twojars_ejb);
        ejb3_misc_moduleName_twojars_ear.addAsModule(ejb3_misc_moduleName_twojars_client);

        // Unpack the ear for the appclient runner
        Path earPath = Paths.get("target", "ejb3_misc_moduleName_twojars.ear");
        Files.createDirectories(earPath);
        Path clientJarPath = earPath.resolve("ejb3_misc_moduleName_twojars_client.jar");
        final ZipExporter zipExporter = ejb3_misc_moduleName_twojars_client.as(ZipExporter.class);
        zipExporter.exportTo(clientJarPath.toFile(), true);

        return ejb3_misc_moduleName_twojars_ear;
    }

    @Test
    @RunAsClient
    public void testAppClient() throws Exception {
        System.out.println("Running testAppClient");
        runClient();
        System.out.println("Finished testAppClient");
    }

    private void runClient() throws Exception {
        String glassfishHome = System.getProperty("glassfish.home");

        Files.list(Paths.get("target/ejb3_misc_moduleName_twojars.ear")).forEach(path -> {
            System.out.println("Unpacked file: " + path);
        });
        File clientDir = null;
        String[] clientCmdLine = {
                glassfishHome+"/glassfish/bin/appclient",
                "-client",
                "target/ejb3_misc_moduleName_twojars.ear/ejb3_misc_moduleName_twojars_client.jar"
        };
        String[] clientEnvp = null;

        Process appClientProcess = Runtime.getRuntime().exec(clientCmdLine, clientEnvp, clientDir);
        System.out.println("Created process" + appClientProcess.info());
        System.out.println("process(%d).envp: %s".formatted(appClientProcess.pid(), Arrays.toString(clientEnvp)));
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(appClientProcess.getInputStream(), StandardCharsets.UTF_8));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(appClientProcess.getErrorStream(), StandardCharsets.UTF_8));

        final Thread readOutputThread = new Thread(() -> readClientProcess(outputReader, false), "stdout reader");
        readOutputThread.start();
        final Thread readErrorThread = new Thread(() -> readClientProcess(errorReader, true), "stderr reader");
        readErrorThread.start();
        System.out.println("Started process reader threads");

        boolean timeout = appClientProcess.waitFor(1000, TimeUnit.SECONDS);
        if (timeout) {
            System.out.println("AppClient process finished");
        } else {
            System.out.println("AppClient process timed out");
            appClientProcess.destroy();
            throw new RuntimeException("AppClient process timed out");
        }
        Assertions.assertEquals(0, appClientProcess.exitValue(), "AppClient process exited with non-zero code");
    }
    private void readClientProcess(BufferedReader reader, boolean errReader) {
        System.out.println("Begin readClientProcess");
        int count = 0;
        try {
            String line = reader.readLine();
            // System.out.println("RCP: " + line);
            while (line != null) {
                count++;
                if (errReader)
                    System.out.println("[stderr] " + line);
                else
                    System.out.println("[stdout] " + line);
                line = reader.readLine();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println(String.format("Exiting(isStderr=%s), read %d lines", errReader, count));
    }
}
