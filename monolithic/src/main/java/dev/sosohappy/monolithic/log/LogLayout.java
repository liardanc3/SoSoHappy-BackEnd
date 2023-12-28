package dev.sosohappy.monolithic.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.CachingDateFormatter;

public class LogLayout extends LayoutBase<ILoggingEvent>{

    CachingDateFormatter cachingDateFormatter = new CachingDateFormatter("YYYY-MM-dd HH:mm:ss.SSS");

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder();

        event.getFormattedMessage();

        String loggerName = event.getLoggerName();
        String loggerNameLog = loggerName.substring(Math.max(loggerName.length() - 40, 0));

        sb.append(cachingDateFormatter.format(event.getTimeStamp())).append(" ")
                .append(String.format("%-5s", event.getLevel())).append(" ")
                .append("---").append(" ")
                .append(String.format("%-40.40s", loggerNameLog)).append(" : ")
                .append(event.getMessage());

        sb.append(CoreConstants.LINE_SEPARATOR);

        return sb.toString();
    }
}