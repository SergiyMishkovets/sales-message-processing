package com.ontomix.smp.jms;

import javax.jms.JMSException;

/**
 * A Message Client Interface
 *
 */
public interface IMessageClient {
    /**
     *
     * @throws JMSException
     * @throws InterruptedException
     */
    void receiveMessages() throws JMSException, InterruptedException;
}
