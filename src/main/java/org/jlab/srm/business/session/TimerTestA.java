package org.jlab.srm.business.session;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//@Singleton
//@Startup
public class TimerTestA {

    private static final Logger LOGGER = Logger.getLogger(TimerTestA.class.getName());

    private Timer timer;
    @Resource
    private TimerService timerService;

    @PostConstruct
    private void init() {
        clearTimer();
        startTimer();
    }

    private void clearTimer() {
        LOGGER.log(Level.INFO, "Clearing Test Timer A");
        for (Timer t : timerService.getTimers()) {
            LOGGER.log(Level.FINE, "Timer Expression: {0}, Remaining: {1}, Next timeout: {2}", new Object[]{t.getSchedule(), t.getTimeRemaining(), t.getNextTimeout()});
            t.cancel();
        }
        timer = null;
    }

    private void startTimer() {
        LOGGER.log(Level.INFO, "Starting Test Timer A");
        ScheduleExpression schedExp = new ScheduleExpression();
        schedExp.second("*");
        schedExp.minute("*");
        schedExp.hour("*");
        TimerConfig config = new TimerConfig();
        config.setPersistent(false);
        timer = timerService.createCalendarTimer(schedExp, config);
    }

    @Timeout
    private void handleTimeout(Timer timer) {
        LOGGER.log(Level.INFO, "handleTimeout: Test Timer A");
    }
}
