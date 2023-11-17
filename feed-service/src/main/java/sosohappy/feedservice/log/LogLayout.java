package sosohappy.feedservice.log;

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

        sb.append("╔══ ");
        sb.append(cachingDateFormatter.format(event.getTimeStamp()));
        sb.append("  ");
        sb.append(event.getLevel());
        sb.append("  ════════════════════════════════════════════════════");
        sb.append("══════════════════════════════════════════════════════");
        sb.append("╗\n");

        sb.append(" ".repeat(142));
        sb.append("\n");

        String[] messageList = event.getFormattedMessage().split("\n");
        for(String message : messageList){
            for(int i=0; i<message.length(); i+=138){
                sb.append(String.format("  %-138.138s", message.substring(i, Math.min(i+138, message.length()))));
                sb.append("\n");
            }
        }

        sb.append("\n╚");
        sb.append("═".repeat(140));
        sb.append("╝" + "\n\n");
        sb.append(CoreConstants.LINE_SEPARATOR);

        return sb.toString();
    }
}