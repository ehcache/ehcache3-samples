/*
 * Copyright Terracotta, Inc.
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
package org.terracotta.sample.collector;

import org.ehcache.Cache;
import org.terracotta.context.ContextManager;
import org.terracotta.context.TreeNode;
import org.terracotta.context.query.Matcher;
import org.terracotta.context.query.Matchers;
import org.terracotta.context.query.Query;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.ValueStatistic;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.terracotta.context.query.Matchers.attributes;
import static org.terracotta.context.query.Matchers.context;
import static org.terracotta.context.query.Matchers.hasAttribute;
import static org.terracotta.context.query.Matchers.identifier;
import static org.terracotta.context.query.Matchers.subclassOf;
import static org.terracotta.context.query.Queries.self;
import static org.terracotta.context.query.QueryBuilder.queryBuilder;

/**
 * @author Ludovic Orban
 */
class Ehcache3Stats {

  public static OperationStatistic findOperationStat(Cache<?, ?> cache1, final String statName, final String tag) {
    Query q = queryBuilder()
        .descendants().filter(context(identifier(subclassOf(OperationStatistic.class)))).build();

    Set<TreeNode> operationStatisticNodes = q.execute(Collections.singleton(ContextManager.nodeFor(cache1)));
    Set<TreeNode> result = queryBuilder()
        .filter(
            context(attributes(Matchers.<Map<String, Object>>allOf(
                hasAttribute("name", statName), hasAttribute("tags", new Matcher<Set<String>>() {
                  @Override
                  protected boolean matchesSafely(Set<String> object) {
                    return object.contains(tag);
                  }
                }))))).build().execute(operationStatisticNodes);

    if (result.size() != 1) {
      throw new RuntimeException("single stat not found; found " + result.size());
    }

    TreeNode node = result.iterator().next();
    return (OperationStatistic) node.getContext().attributes().get("this");
  }

  public static ValueStatistic findValueStat(Cache<?, ?> cache1, final String statName, final String tag) {
    Query q = queryBuilder().chain(self())
        .descendants().filter(context(identifier(subclassOf(ValueStatistic.class)))).build();

    Set<TreeNode> nodes = q.execute(Collections.singleton(ContextManager.nodeFor(cache1)));
    Set<TreeNode> result = queryBuilder()
        .filter(
            context(attributes(Matchers.<Map<String, Object>>allOf(
                hasAttribute("name", statName), hasAttribute("tags", new Matcher<Set<String>>() {
                  @Override
                  protected boolean matchesSafely(Set<String> object) {
                    return object.contains(tag);
                  }
                }))))).build().execute(nodes);

    if (result.size() != 1) {
      throw new RuntimeException("single stat not found; found " + result.size());
    }

    TreeNode node = result.iterator().next();
    return (ValueStatistic) node.getContext().attributes().get("this");
  }

}
