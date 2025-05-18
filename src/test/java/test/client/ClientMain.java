package test.client;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import test.ejb.AppResRemoteIF;

import static test.ejb.ServiceLocator.lookupNoTry;

public class ClientMain {
    @Resource(lookup = "java:app/AppName")
    static String appName;
    @Resource(lookup = "java:module/ModuleName")
    static String moduleName;
    //@EJB(name = "moduleBean", lookup = "java:global/ejb3_misc_moduleName_twojars/renamed_twojars_ejb/ModuleBean")
    @EJB(name = "moduleBean", lookup = "java:global/ejb3_misc_moduleName_twojars/ejb3_misc_moduleName_twojars_ejb/ModuleBean")
    static AppResRemoteIF moduleBean;
    @EJB(name = "module2Bean", lookup = "java:global/renamed2_twojars_ejb/Module2Bean")
    static AppResRemoteIF module2Bean;

    public static void main(String[] args) {
        System.out.println("ClientMain.main() called");
        System.out.println("java:app/AppName: "+appName);
        System.out.println("java:module/ModuleName: "+moduleName);
        System.out.println("moduleBean: "+moduleBean);

        StringBuilder sb = moduleBean.getPostConstructRecords();
        System.out.println("moduleBean.getPostConstructRecords(): "+sb);

        AppResRemoteIF lookupResult = (AppResRemoteIF) lookupNoTry("java:global/renamed2_twojars_ejb/Module2Bean");

        System.out.println("java:global/renamed2_twojars_ejb/Module2Bean: "+lookupResult);
        //lookupResult = (AppResRemoteIF) lookupNoTry("java:app/renamed_twojars_ejb/ModuleBean");
        lookupResult = (AppResRemoteIF) lookupNoTry("java:app/ejb3_misc_moduleName_twojars_ejb/ModuleBean");
        System.out.println("java:app/renamed_twojars_ejb/ModuleBean: "+lookupResult);

        System.out.println("moduleBean.getName(): "+moduleBean.getName());
    }
}
