package com.ontomix.smp;

import com.ontomix.smp.jms.IMessageClient;
import com.ontomix.smp.jms.ActiveMQMessageClient;

import javax.jms.JMSException;

/**
 * Sales Message Processing App
 *
 * @author michaelx
 */
public class App {

    private static App APP;

    public static void main(String[] args) {
        APP = new App();
        APP.launch(args);
    }

    public void launch(String[] args) {
        System.out.println("Sales Message Processing App");
        String brokerUrl;
        String username;
        String password;
        if (null != args && args.length == 3) {
            brokerUrl = args[0];
            username = args[1];
            password = args[2];
        } else {
            // Default broker URL
            brokerUrl = "tcp://localhost:61616";
            username = "admin";
            password = "admin";
        }
        IMessageClient client;
        client = new ActiveMQMessageClient(brokerUrl, username, password);
        try {
            client.receiveMessages();
        } catch (JMSException | InterruptedException e) {
            System.out.println("Caught " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
