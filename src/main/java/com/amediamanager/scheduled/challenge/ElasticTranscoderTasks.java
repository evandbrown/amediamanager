package com.amediamanager.scheduled.challenge;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.model.Message;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ElasticTranscoderTasks extends com.amediamanager.scheduled.ElasticTranscoderTasks {
	@Override
	@Scheduled(fixedDelay = 1)
    public void checkStatus() {
		/**
		 * Call super.handleMessage for each message you receive from the queue
		 */
        super.checkStatus();
    }
	
	@Override
	public void deleteMessage(final Message message) {
		super.deleteMessage(message);
	}
}
