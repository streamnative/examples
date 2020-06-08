package io.streamnative.examples.pubsub.clickstream;

import lombok.Data;

@Data
public class ClientEvent {

    public long timestamp;
    public String ip;
    public String request;
    public int status;
    public int userid;
    public int bytes;
    public String agent;

}
