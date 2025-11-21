package br.com.jbProjects.helper;

import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("br.com.jbProjects") // pacote onde estão seus testes
public class AllTestsRunner {

    @BeforeSuite
    static void globalSetup() {
        System.out.println(">> Inicializando EM GLOBAL");
        JPAHelper.entityManager();
    }

    @AfterSuite
    static void globalShutdown() {
        System.out.println(">> Encerrando EM GLOBAL");
        JPAHelper.shutdown();
    }
}
