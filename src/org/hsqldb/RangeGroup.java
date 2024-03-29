/* Copyright (c) 2001-2019, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.hsqldb;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Fred Toussi (fredt@users dot sourceforge.net)
 * @version 2.5.0
 * @since 2.2.9
 */
public interface RangeGroup {

    RangeGroup   emptyGroup = new RangeGroupEmpty();
    RangeGroup[] emptyArray = new RangeGroup[]{ emptyGroup };

    RangeVariable[] getRangeVariables();

    void setCorrelated();

    boolean isVariable();

    class RangeGroupSimple implements RangeGroup {

        public String toString(){
            return Arrays.stream(ranges).map(String::valueOf).collect(Collectors.joining(", "));
        }

        final RangeVariable[] ranges;
        final RangeGroup      baseGroup;
        final TableDerived    table;
        final boolean         isVariable;

        public RangeGroupSimple(TableDerived table) {

            this.ranges     = RangeVariable.emptyArray;
            this.baseGroup  = null;
            this.table      = table;
            this.isVariable = false;
        }

        public RangeGroupSimple(RangeVariable[] ranges, RangeGroup baseGroup) {

            this.ranges     = ranges;
            this.baseGroup  = baseGroup;
            this.table      = null;
            this.isVariable = false;
        }

        public RangeGroupSimple(RangeVariable[] ranges, boolean isVariable) {

            this.ranges     = ranges;
            this.baseGroup  = null;
            this.table      = null;
            this.isVariable = isVariable;
        }

        public RangeVariable[] getRangeVariables() {
            return ranges;
        }

        public void setCorrelated() {

            if (baseGroup != null) {
                baseGroup.setCorrelated();
            }

            if (table != null) {
                table.setCorrelated();
            }
        }

        public boolean isVariable() {
            return isVariable;
        }
    }

    class RangeGroupEmpty implements RangeGroup {

        RangeGroupEmpty() {

            //
        }

        public RangeVariable[] getRangeVariables() {
            return RangeVariable.emptyArray;
        }

        public void setCorrelated() {

            //
        }

        public boolean isVariable() {
            return false;
        }
    }
}
