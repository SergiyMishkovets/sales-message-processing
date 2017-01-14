package com.ontomix.smp.service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

/**
 * A Sales Message Processing Service Interface
 */
public interface ISalesMessageProcessingService {
    void process(TextMessage textMessage) throws JMSException, JAXBException;
}
