/*
 * Copyright 2022 Eric Medvet <eric.medvet@gmail.com> (as eric)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.units.erallab.mrsim.engine;

import com.google.common.util.concurrent.AtomicDouble;
import it.units.erallab.mrsim.core.*;
import it.units.erallab.mrsim.core.actions.AddAgent;
import it.units.erallab.mrsim.core.bodies.Body;
import it.units.erallab.mrsim.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author "Eric Medvet" on 2022/07/06 for 2dmrsim
 */
public abstract class AbstractEngine implements Engine {

  @FunctionalInterface
  public interface ActionSolver<A extends Action<O>, O> {
    O solve(A action, Agent agent) throws ActionException;
  }

  protected final AtomicDouble t;
  protected final List<Pair<Agent, List<ActionOutcome<?>>>> agentPairs;
  private final Map<Class<? extends Action<?>>, ActionSolver<?, ?>> actionSolvers;
  private final static Logger L = Logger.getLogger(AbstractEngine.class.getName());

  private final AtomicInteger nOfTicks;
  private final AtomicDouble engineT;
  private final Instant startingInstant;
  private final AtomicInteger nOfActions;
  private final AtomicInteger nOfUnsupportedActions;
  private final AtomicInteger nOfIllegalActions;


  public AbstractEngine() {
    agentPairs = new ArrayList<>();
    actionSolvers = new LinkedHashMap<>();
    t = new AtomicDouble(0d);
    nOfTicks = new AtomicInteger(0);
    engineT = new AtomicDouble(0d);
    startingInstant = Instant.now();
    nOfActions = new AtomicInteger(0);
    nOfUnsupportedActions = new AtomicInteger(0);
    nOfIllegalActions = new AtomicInteger(0);
    registerActionSolvers();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Snapshot tick() {
    Instant tickStartingInstant = Instant.now();
    nOfTicks.incrementAndGet();
    for (int i = 0; i < agentPairs.size(); i++) {
      List<ActionOutcome<?>> outcomes = new ArrayList<>();
      for (Action<?> action : agentPairs.get(i).first().act(t.get(), agentPairs.get(i).second())) {
        outcomes.add(new ActionOutcome<>(action, (Optional) perform(action, agentPairs.get(i).first())));
      }
      Pair<Agent, List<ActionOutcome<?>>> pair = new Pair<>(
          agentPairs.get(i).first(),
          outcomes
      );
      agentPairs.set(i, pair);
    }
    double newT = innerTick();
    t.set(newT);
    engineT.addAndGet(Duration.between(tickStartingInstant, Instant.now()).toMillis() / 1000d);
    return new EngineSnapshot(
        t.get(),
        agentPairs,
        getBodies(),
        engineT.get(),
        Duration.between(startingInstant, Instant.now()).toMillis() / 1000d,
        nOfTicks.get(),
        nOfActions.get(),
        nOfUnsupportedActions.get(),
        nOfIllegalActions.get()
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A extends Action<O>, O> Optional<O> perform(A action, Agent agent) {
    nOfActions.incrementAndGet();
    ActionSolver<A, O> actionSolver = (ActionSolver<A, O>) actionSolvers.get(action.getClass());
    if (actionSolver == null) {
      L.finer(String.format("Ignoring unsupported action: %s", action.getClass().getSimpleName()));
      nOfUnsupportedActions.incrementAndGet();
      return Optional.empty();
    }
    try {
      O outcome = actionSolver.solve(action, agent);
      return outcome == null ? Optional.empty() : Optional.of(outcome);
    } catch (ActionException e) {
      L.finer(String.format("Ignoring illegal action: %s", e));
      nOfIllegalActions.incrementAndGet();
      return Optional.empty();
    } catch (RuntimeException e) {
      L.warning(String.format("Ignoring action throwing exception: %s", e));
      nOfIllegalActions.incrementAndGet();
      return Optional.empty();
    }
  }

  protected abstract double innerTick();

  protected abstract Collection<Body> getBodies();

  protected final <A extends Action<O>, O> void registerActionSolver(
      Class<A> actionClass,
      ActionSolver<A, O> actionSolver
  ) {
    actionSolvers.put(actionClass, actionSolver);
  }

  protected void registerActionSolvers() {
    registerActionSolver(AddAgent.class, (action, agent) -> {
      agentPairs.add(new Pair<>(action.agent(), List.of()));
      return null;
    });
  }

  @Override
  public double t() {
    return t.get();
  }
}