package de.unidisk.rest;

import de.unidisk.view.TestSetupBean;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class LifecycleListener implements ServletContextListener {

    TestSetupBean testSetupBean = TestSetupBean.Default();

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try{
            testSetupBean.init();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
