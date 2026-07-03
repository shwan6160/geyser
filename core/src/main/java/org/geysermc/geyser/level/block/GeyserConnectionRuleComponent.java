/*
 * Copyright (c) 2019-2026 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.level.block;

import org.geysermc.geyser.api.block.custom.component.ConnectionRuleComponent;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class GeyserConnectionRuleComponent implements ConnectionRuleComponent {
    private final AcceptsConnectionsFrom acceptsConnectionsFrom;
    private final Set<Direction> enabledDirections;

    GeyserConnectionRuleComponent(Builder builder) {
        this.acceptsConnectionsFrom = Objects.requireNonNull(builder.acceptsConnectionsFrom, "acceptsConnectionsFrom");
        this.enabledDirections = builder.enabledDirections == null ? null : Set.copyOf(builder.enabledDirections);
    }

    @Override
    public AcceptsConnectionsFrom acceptsConnectionsFrom() {
        return acceptsConnectionsFrom;
    }

    @Override
    public @Nullable Set<Direction> enabledDirections() {
        return enabledDirections;
    }

    public static class Builder implements ConnectionRuleComponent.Builder {
        private AcceptsConnectionsFrom acceptsConnectionsFrom;
        private Set<Direction> enabledDirections;

        @Override
        public Builder acceptsConnectionsFrom(AcceptsConnectionsFrom acceptsConnectionsFrom) {
            this.acceptsConnectionsFrom = acceptsConnectionsFrom;
            return this;
        }

        @Override
        public Builder enabledDirections(@Nullable Set<Direction> enabledDirections) {
            this.enabledDirections = enabledDirections;
            return this;
        }

        @Override
        public ConnectionRuleComponent build() {
            return new GeyserConnectionRuleComponent(this);
        }
    }
}
