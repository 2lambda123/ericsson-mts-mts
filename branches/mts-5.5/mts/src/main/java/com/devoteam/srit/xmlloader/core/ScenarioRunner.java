/*
 * ScenarioRunner.java
 *
 * Created on 30 mai 2007, 10:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.devoteam.srit.xmlloader.core;

import com.devoteam.srit.xmlloader.core.exception.InterruptedExecutionException;
import com.devoteam.srit.xmlloader.core.log.GlobalLogger;
import com.devoteam.srit.xmlloader.core.protocol.BufferMsg;
import com.devoteam.srit.xmlloader.core.protocol.Msg;
import com.devoteam.srit.xmlloader.core.log.TextEvent;
import com.devoteam.srit.xmlloader.core.log.TextListenerKey;
import com.devoteam.srit.xmlloader.core.log.TextListenerProviderRegistry;
import com.devoteam.srit.xmlloader.core.newstats.StatKey;
import com.devoteam.srit.xmlloader.core.newstats.StatPool;

import com.devoteam.srit.xmlloader.core.utils.hierarchy.DefaultHierarchyMember;
import com.devoteam.srit.xmlloader.core.utils.hierarchy.HierarchyMember;
import com.devoteam.srit.xmlloader.core.utils.notifications.DefaultNotificationSender;
import com.devoteam.srit.xmlloader.core.utils.notifications.Notification;
import com.devoteam.srit.xmlloader.core.utils.notifications.NotificationListener;
import com.devoteam.srit.xmlloader.core.utils.notifications.NotificationSender;
import java.util.List;

/**
 *
 * @author gpasquiers
 */
public class ScenarioRunner extends Runner
        implements TextListenerKey, Runnable,
        HierarchyMember<TestcaseRunner, Object>,
        NotificationSender<Notification<String, RunnerState>> {

    private DefaultHierarchyMember<TestcaseRunner, Object> defaultHierarchyMember;

    // <editor-fold defaultstate="collapsed" desc="DefaultHierarchyMember Implementation">
    public TestcaseRunner getParent() {
        return this.defaultHierarchyMember.getParent();
    }

    public List<Object> getChildren() {
        throw new RuntimeException("This HierarchyMember CANNOT have any children");
    }

    public void setParent(TestcaseRunner parent) {
        this.defaultHierarchyMember.setParent(parent);
    }

    public void addChild(Object child) {
        throw new RuntimeException("This HierarchyMember CANNOT have any children");
    }

    public void removeChild(Object child) {
        throw new RuntimeException("This HierarchyMember CANNOT have any children");
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="NotificationSender Implementation">
    private DefaultNotificationSender<Notification<String, RunnerState>> defaultNotificationSender;

    public void addListener(NotificationListener<Notification<String, RunnerState>> listener) {
        this.defaultNotificationSender.addListener(listener);

        listener.notificationReceived(new Notification<String, RunnerState>(this.getName(), getState().clone()));
    }

    public void removeListener(NotificationListener listener) {
        this.defaultNotificationSender.removeListener(listener);
    }

    public void notifyAll(Notification<String, RunnerState> notification) {
        this.defaultNotificationSender.notifyAll(notification);
    }

    public void doNotifyAll() {
        this.defaultNotificationSender.notifyAll(new Notification<String, RunnerState>(this.getName(), getState().clone()));
    }
    // </editor-fold>
    private Scenario _scenario;
    private ThreadRunner _thread;
    private BufferMsg _bufferMsg;
    private long startTimestamp;
    private boolean _stopped;
    private int finallyCount;

    /** Creates a new instance of ScenarioRunner */
    public ScenarioRunner(TestcaseRunner aTestcaseRunner, Scenario scenario) {
        super(scenario.getName());

        defaultHierarchyMember = new DefaultHierarchyMember<TestcaseRunner, Object>();
        defaultNotificationSender = new DefaultNotificationSender<Notification<String, RunnerState>>();

        defaultHierarchyMember.setParent(aTestcaseRunner);

        setParameterPool(new ParameterPool(this, ParameterPool.Level.scenario, this.getParent().getParameterPool()));

        _stopped = false;
        _bufferMsg = new BufferMsg();
        _thread = null;
        _scenario = scenario;
        setParent(aTestcaseRunner);

        getState()._executionsEnd = 1;
    }

    public void assertIsNotInterrupting() throws InterruptedExecutionException {
        if (getState().isInterrupted() || getState().isFinished()) {
            if (_stopped && !isInFinally()) {
                throw new InterruptedExecutionException("scenario runner is currently stopping or already stopped " + "getState()=" + getState());
            }
        }
    }

    public void reset() {
        resetState();
        doNotifyAll();
    }

    public void resetToOpened() {
        resetState();
        getState().setFlag(RunnerState.F_OPENED, true);
        doNotifyAll();
    }

    /**
     * Starts the thread of the ScenarioRunner
     */
    synchronized public void start() {
        try {
            assertIsNotInterrupting();
            GlobalLogger.instance().getSessionLogger().debug(this, TextEvent.Topic.CORE, "ScenarioRunner started");
            getState().setFlag(RunnerState.F_STARTED, true);
            getState()._executionsCurrent = 0;
            getState()._progression = 0;
            getParameterPool().clear();

            Parameter parameter = new Parameter();
            parameter.add(getScenario().getName());
            getParameterPool().set("[scenarioName]", parameter);

            parameter = new Parameter();
            parameter.add(getScenario().getId());
            getParameterPool().set("[scenarioId]", parameter);

            doNotifyAll();
            _thread = ThreadPool.reserve().start(this);
        }
        catch (InterruptedExecutionException e) {
            GlobalLogger.instance().getSessionLogger().error(this, TextEvent.Topic.CORE, e, "ScenarioRunner exception");
            GlobalLogger.instance().getApplicationLogger().error(TextEvent.Topic.CORE, e, "ScenarioRunner exception");
            getState().setFlag(RunnerState.F_INTERRUPTED, true);
            getState().setFlag(RunnerState.F_FINISHED, true);
            doNotifyAll();
        }
        catch (Exception e) {
            GlobalLogger.instance().getSessionLogger().error(this, TextEvent.Topic.CORE, e, "ScenarioRunner exception");
            GlobalLogger.instance().getApplicationLogger().error(TextEvent.Topic.CORE, e, "ScenarioRunner exception");
            getState().setFlag(RunnerState.F_FAILED, true);
            getState().setFlag(RunnerState.F_FINISHED, true);
            doNotifyAll();
        }
    }

    /**
     * Stops the thread of the ScenarioRunner
     */
    public synchronized void stop() {
        _stopped = true;

        getState().setFlag(RunnerState.F_INTERRUPTED, true);

        if (!isInFinally()) {
            if (_thread != null) {
                _thread.interrupt();
            }
        }
    }

    synchronized public boolean isInFinally() {
        return finallyCount > 0;
    }

    synchronized public void finallyEnter() {
        finallyCount++;

        // consume interrupted flag
        Thread.interrupted();
    }

    synchronized public void finallyExit() {
        finallyCount--;
    }

    /**
     * Method executed by the thread
     */
    public void run() {
        Thread.interrupted();

        /**
         * Update logs and statistics: a new scenario is currently running
         */
        StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_name"), _scenario.getName());
        StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_description"), _scenario.getDescription());
        StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_startNumber"), 1);
        StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_currentNumber"), 1);
        GlobalLogger.instance().getSessionLogger().info(this, TextEvent.Topic.CORE, "Scenario running");

        /**
         * Backup the start time of the scenario
         */
        startTimestamp = System.currentTimeMillis();

        /**
         * Then execute the operations of the scenario
         */
        try {
            _scenario.executeScenario(this);
        }
        catch (Exception e) {
            GlobalLogger.instance().getSessionLogger().error(this, TextEvent.Topic.CORE, e, "Exception in ScenarioRunner\n");
            GlobalLogger.instance().getApplicationLogger().error(TextEvent.Topic.CORE, e, "Exception in ScenarioRunner\n");

            /**
             * If any Exception happen while executing the operations then the
             * test go in FAILING state if he is not already in INTERRUPTING state
             * because of the stop() method.
             */
            if (!getState().isInterrupted()) {
                getState().setFlag(RunnerState.F_FAILED, true);
            }
            doNotifyAll();
        }

        /**
         * Then execute the operations of the finally
         */
        this.finallyEnter();
        try {
            _scenario.executeFinally(this);
        }
        catch (Exception e) {

            GlobalLogger.instance().getSessionLogger().error(this, TextEvent.Topic.CORE, e, "Exception in finally in ScenarioRunner\n");
            GlobalLogger.instance().getApplicationLogger().error(TextEvent.Topic.CORE, e, "Exception in finally in ScenarioRunner\n");

            /**
             * If any Exception happen while executing the operations then the
             * test go in FAILING state if he is not already in INTERRUPTING state
             * because of the stop() method.
             */
            if (!getState().isInterrupted()) {
                getState().setFlag(RunnerState.F_FAILED, true);
            }
            doNotifyAll();
        }

        this._thread = null;
        this.finallyExit();

        /**
         * Update the statistics : a scenario ended
         */
        StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_currentNumber"), -1);
        StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_completeNumber"), 1);

        /**
         * Add the duration of the execution to the statistics and to the logs
         */
        long endTimestamp = System.currentTimeMillis();
        float duration_stats = ((float) (endTimestamp - startTimestamp) / 1000);
        StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_durationTime"), duration_stats);



        /**
         * Finalize and clean up things.
         */
        this._bufferMsg.clear();

        GlobalLogger.instance().getSessionLogger().debug(this, TextEvent.Topic.CORE, "ScenarioRunner ended: notify TestcaseRunner");

        /**
         * Compute the final state of the scenario and increments counters
         * accordingly to that state.
         * Putting the scenario state in a final test should be the last thing to do.
         */
        getState()._executionsCurrent = 1;

        if (getState().isInterrupted()) {
            GlobalLogger.instance().getSessionLogger().error(this, TextEvent.Topic.CORE, "ScenarioRunner interrupted (duration=", duration_stats, "s)");
            StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_failedNumber"), 1);
        }
        else if (getState().isFailed()) {
            GlobalLogger.instance().getSessionLogger().error(this, TextEvent.Topic.CORE, "ScenarioRunner KO (duration=", duration_stats, "s)");
            StatPool.getInstance().addValue(new StatKey(StatPool.PREFIX_SCENARIO, _scenario.getName(), "_failedNumber"), 1);
        }
        else {
            GlobalLogger.instance().getSessionLogger().info(this, TextEvent.Topic.CORE, "ScenarioRunner OK (duration=", duration_stats, "s)");
        }

        TextListenerProviderRegistry.instance().dispose(this);

        getState().setFlag(RunnerState.F_FINISHED, true);
        doNotifyAll();
    }

    public Scenario getScenario() {
        return _scenario;
    }

    /** adds a message to the stack and notify the Thread waiting for it */
    public void dispatchMessage(Msg msg) {
        _bufferMsg.dispatchMessage(msg);
    }

    public BufferMsg getBufferMsg() {
        return _bufferMsg;
    }

    public void stackFunctionParameterPool() {
        ParameterPool parameterPool = new ParameterPool(this, ParameterPool.Level.function, getParameterPool());
        setParameterPool(parameterPool);
    }

    public void unstackFunctionParameterPool() {
        if (getParameterPool().level == ParameterPool.Level.function) {
            setParameterPool(getParameterPool().getParent());
        }
    }
    /** Implicit message for setFromMessage operation */
    private Msg currentMsg = null;

    public Msg getCurrentMsg() {
        return currentMsg;
    }

    public void setCurrentMsg(Msg currentMsg) {
        this.currentMsg = currentMsg;
    }

    public String toString() {
        return _scenario.toString();
    }
}
