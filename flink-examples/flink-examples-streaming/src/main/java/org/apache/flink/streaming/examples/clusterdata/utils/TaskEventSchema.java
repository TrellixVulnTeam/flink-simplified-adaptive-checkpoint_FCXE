package org.apache.flink.streaming.examples.clusterdata.utils;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.examples.clusterdata.datatypes.EventType;
import org.apache.flink.streaming.examples.clusterdata.datatypes.TaskEvent;

/**
 * Implements a SerializationSchema and DeserializationSchema for TaskEvent for Kafka data sources
 * and sinks.
 */
public class TaskEventSchema
        implements DeserializationSchema<TaskEvent>, SerializationSchema<TaskEvent> {

    @Override
    public byte[] serialize(TaskEvent element) {

        return element.toString().getBytes();
    }

    @Override
    public TaskEvent deserialize(byte[] message) {
        // TODO: we don't write them to Kafka in the same order we read them from the gzip file!
        try {
            Thread.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String line = new String(message);
        String[] tokens = line.split(",");
        if (tokens.length < 12) {
            throw new RuntimeException(
                    "Invalid task event record: " + line + ", tokens: " + tokens.length);
        }

        TaskEvent tEvent = new TaskEvent();

        try {
            tEvent.jobId = Long.parseLong(tokens[0]);
            tEvent.taskIndex = Integer.parseInt(tokens[1]);
            tEvent.timestamp = Long.parseLong(tokens[2]);
            tEvent.machineId = Long.parseLong(tokens[3]);
            if (tokens[4].equals("SUBMIT")) {
                tEvent.eventType = EventType.SUBMIT;
            } else if (tokens[4].equals("SCHEDULE")) {
                tEvent.eventType = EventType.SCHEDULE;
            } else if (tokens[4].equals("EVICT")) {
                tEvent.eventType = EventType.EVICT;
            } else if (tokens[4].equals("FAIL")) {
                tEvent.eventType = EventType.FAIL;
            } else if (tokens[4].equals("FINISH")) {
                tEvent.eventType = EventType.FINISH;
            } else if (tokens[4].equals("KILL")) {
                tEvent.eventType = EventType.KILL;
            } else if (tokens[4].equals("LOST")) {
                tEvent.eventType = EventType.LOST;
            } else if (tokens[4].equals("UPDATE_PENDING")) {
                tEvent.eventType = EventType.UPDATE_PENDING;
            } else if (tokens[4].equals("UPDATE_RUNNING")) {
                tEvent.eventType = EventType.UPDATE_RUNNING;
            }
            tEvent.username = tokens[5];
            tEvent.schedulingClass = Integer.parseInt(tokens[6]);
            tEvent.priority = Integer.parseInt(tokens[7]);
            tEvent.maxCPU = Double.parseDouble(tokens[8]);
            tEvent.maxRAM = Double.parseDouble(tokens[9]);
            tEvent.maxDisk = Double.parseDouble(tokens[10]);
            tEvent.differentMachine = Boolean.parseBoolean(tokens[11]);
            if (tokens.length > 12) {
                tEvent.missingInfo = tokens[12];
            }
        } catch (NumberFormatException nfe) {
            throw new RuntimeException(
                    "Invalid message record while reading from Kafka: " + line, nfe);
        }
        return tEvent;
    }

    @Override
    public boolean isEndOfStream(TaskEvent nextElement) {
        return false;
    }

    @Override
    public TypeInformation<TaskEvent> getProducedType() {
        return TypeExtractor.getForClass(TaskEvent.class);
    }
}
