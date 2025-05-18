package test.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;

import static test.ejb.ServiceLocator.lookupNoTry;
import static test.ejb.ServiceLocator.lookupShouldFail;

@Singleton
public class ModuleBean extends AppResBeanBase implements AppResRemoteIF {
    @EJB
    private AppResRemoteIF moduleBean;

    @Resource(lookup = "java:module/ModuleName")
    private String moduleName;

    @Resource(lookup = "java:app/AppName")
    private String appName;

    private void nonPostConstruct() {
        lookupShouldFail("java:app/ejb3_misc_moduleName_twojars_client/ModuleBean",
                postConstructRecords);
        lookupShouldFail("java:app/ejb3_misc_moduleName_twojars_ejb/ModuleBean",
                postConstructRecords);
        lookupShouldFail(
                "java:global/ejb3_misc_moduleName_twojars/ejb3_misc_moduleName_twojars_ejb/ModuleBean",
                postConstructRecords);

        lookupShouldFail("java:global/two_standalone_component_ejb/Module2Bean",
                postConstructRecords);

        System.out.println(postConstructRecords.toString());

        Helper.assertNotEquals(null, null, moduleBean, postConstructRecords);

        AppResRemoteIF lookupResult = null;
        String[] names = { "java:module/ModuleBean",
                "java:app/renamed_twojars_ejb/ModuleBean",
                "java:global/ejb3_misc_moduleName_twojars/renamed_twojars_ejb/ModuleBean",

                "java:global/renamed2_twojars_ejb/Module2Bean" };
        for (String name : names) {
            postConstructRecords.append("About to look up " + name);
            lookupResult = (AppResRemoteIF) lookupNoTry(name);
            Helper.assertNotEquals(null, null, lookupResult, postConstructRecords);
            lookupResult = null;
        }
    }

    @Override
    public StringBuilder getPostConstructRecords() {
        nonPostConstruct();
        return super.getPostConstructRecords();
    }

}
