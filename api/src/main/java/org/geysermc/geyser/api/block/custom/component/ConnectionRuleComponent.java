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

package org.geysermc.geyser.api.block.custom.component;

import org.geysermc.geyser.api.GeyserApi;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * This class is used to store data for a connection rule component.
 * Equivalent to "minecraft:connection_rule".
 *
 * @since 2.9.5
 */
public interface ConnectionRuleComponent {

    /**
     * Gets what block types this block accepts visual connections from.
     *
     * @return the accepted connection source type
     * @since 2.9.5
     */
    AcceptsConnectionsFrom acceptsConnectionsFrom();

    /**
     * Gets the directions this connection rule applies to.
     *
     * @return the enabled directions, or null for all directions
     * @since 2.9.5
     */
    @Nullable Set<Direction> enabledDirections();

    /**
     * Creates a builder for ConnectionRuleComponent.
     *
     * @return a builder for ConnectionRuleComponent
     * @since 2.9.5
     */
    static Builder builder() {
        return GeyserApi.api().provider(Builder.class);
    }

    enum AcceptsConnectionsFrom {
        NONE("none"),
        ALL("all"),
        ONLY_FENCES("only_fences");

        private final String bedrockName;

        AcceptsConnectionsFrom(String bedrockName) {
            this.bedrockName = bedrockName;
        }

        /**
         * Gets the Bedrock component value.
         *
         * @return the Bedrock component value
         * @since 2.9.5
         */
        public String bedrockName() {
            return bedrockName;
        }
    }

    enum Direction {
        EAST("east"),
        NORTH("north"),
        SOUTH("south"),
        WEST("west");

        private final String bedrockName;

        Direction(String bedrockName) {
            this.bedrockName = bedrockName;
        }

        /**
         * Gets the Bedrock component value.
         *
         * @return the Bedrock component value
         * @since 2.9.5
         */
        public String bedrockName() {
            return bedrockName;
        }
    }

    interface Builder {
        /**
         * Sets what block types this block accepts visual connections from.
         *
         * @param acceptsConnectionsFrom the accepted connection source type
         * @return this builder
         * @since 2.9.5
         */
        Builder acceptsConnectionsFrom(AcceptsConnectionsFrom acceptsConnectionsFrom);

        /**
         * Sets the directions this connection rule applies to.
         *
         * @param enabledDirections the enabled directions, or null for all directions
         * @return this builder
         * @since 2.9.5
         */
        Builder enabledDirections(@Nullable Set<Direction> enabledDirections);

        /**
         * Builds this ConnectionRuleComponent.
         *
         * @return the built ConnectionRuleComponent
         * @since 2.9.5
         */
        ConnectionRuleComponent build();
    }
}
