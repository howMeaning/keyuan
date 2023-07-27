package com.keyuan.utils;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/21
 **/

public class RabbitContent {
    public static final String EXCHANGE_NAME = "topic.exchange";

    public static final String QUEUE_NAME = "normal.queue";
    public static final String NORMALTODEAD="normal.dead.queue";
    public static final String DEADEXCHANGE_NAME = "dead.exchange";
    public static final String DEADQUEUE_NAME = "dead.queue";
    public static final String NORMAL_ROUTING_KEY = "*.normal";
    public static final String DEAD_ROUNTING_KEY="dead";
    public static final String NORMALTODEAD_ROUTING_KEY = "*.dead";
    public static final String ERROREXCHANGE_NAME = "error.exchange";
    public static final String ERRORQUEUE_NAME = "error.queue";
    public static final String ERROR_ROUTINGKEY = "error";
/*
    public static final String CANCEL_EXCHANGE = "cancel.exchange";
    public static final String CANCEL_QUEUE = "cancel.queue";
    public static final String CANCEL_ROUTINGKEY = "cancel";
*/


}
