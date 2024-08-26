package org.jlab.srm.business.session;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

// @Singleton
// @Startup
public class TimerTestB {

  private static final Logger LOGGER = Logger.getLogger(TimerTestB.class.getName());

  private Timer timer;
  @Resource private TimerService timerService;

  @PostConstruct
  private void init() {
    clearTimer();
    startTimer();
  }

  private void clearTimer() {
    LOGGER.log(Level.INFO, "Clearing Test Timer B");
    for (Timer t : timerService.getTimers()) {
      LOGGER.log(
          Level.FINE,
          "Timer Expression: {0}, Remaining: {1}, Next timeout: {2}",
          new Object[] {t.getSchedule(), t.getTimeRemaining(), t.getNextTimeout()});
      t.cancel();
    }
    timer = null;
  }

  private void startTimer() {
    LOGGER.log(Level.INFO, "Starting Test Timer B");
    ScheduleExpression schedExp = new ScheduleExpression();
    schedExp.second("*");
    schedExp.minute("*");
    schedExp.hour("*");
    timer = timerService.createCalendarTimer(schedExp);
  }

  @Timeout
  private void handleTimeout(Timer timer) {
    LOGGER.log(Level.INFO, "handleTimeout: Test Timer B");
  }
}
