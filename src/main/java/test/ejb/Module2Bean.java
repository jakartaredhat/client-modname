package test.ejb;


import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import static test.ejb.ServiceLocator.lookupNoTry;
import static test.ejb.ServiceLocator.lookupShouldFail;

@Singleton
public class Module2Bean extends AppResBeanBase implements AppResRemoteIF {
    @EJB
    private AppResRemoteIF module2Bean;
    @Resource(lookup = "java:module/ModuleName")
    private String moduleName;


    private void nonPostConstruct() {
        lookupShouldFail("java:app/ejb3_misc_moduleName_twojars_client/ModuleBean",
                postConstructRecords);
        lookupShouldFail("java:app/ejb3_misc_moduleName_twojars_ejb/ModuleBean",
                postConstructRecords);
        lookupShouldFail(
                "java:global/ejb3_misc_moduleName_twojars/ejb3_misc_moduleName_twojars_ejb/ModuleBean",
                postConstructRecords);

        lookupShouldFail("java:app/two_standalone_component_ejb/Module2Bean",
                postConstructRecords);
        lookupShouldFail("java:global/two_standalone_component_ejb/Module2Bean",
                postConstructRecords);

        System.out.println(postConstructRecords.toString());

        Helper.assertNotEquals(null, null, module2Bean, postConstructRecords);

        AppResRemoteIF lookupResult = null;
        String[] names = { "java:module/Module2Bean",
                "java:app/renamed2_twojars_ejb/Module2Bean",
                "java:global/renamed2_twojars_ejb/Module2Bean" };
        for (String name : names) {
            postConstructRecords.append("About to look up " + name);
            lookupResult = (AppResRemoteIF) lookupNoTry(name);
            Helper.assertNotEquals(null, null, lookupResult, postConstructRecords);
            lookupResult = null;
        }

        String expected = "renamed2_twojars_ejb";
        String lookup = "java:module/ModuleName";
        String value = (String) lookupNoTry(lookup);
        Helper.assertEquals("Check " + lookup, expected, value,
                postConstructRecords);

        lookup = "java:app/AppName";
        value = (String) lookupNoTry(lookup);
        Helper.assertEquals("Check " + lookup, expected, value,
                postConstructRecords);
    }

    @Override
    public StringBuilder getPostConstructRecords() {
        nonPostConstruct();
        return super.getPostConstructRecords();
    }

    @Override
    public String getName() {
        return moduleName;
    }
}
