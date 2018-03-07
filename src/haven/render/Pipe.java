/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven.render;

import java.util.*;
import haven.render.State.Slot;
import static haven.Utils.eq;

public interface Pipe {
    public <T extends State> T get(Slot<T> slot);
    public Pipe copy();

    /* Should be used sparingly, and the return-value is read-only. */
    public State[] states();

    public default <T extends State> void put(Slot<? super T> slot, T state) {
	throw(new UnsupportedOperationException(this.getClass().toString() + "::put()"));
    }

    public default void copy(Pipe from) {
	throw(new UnsupportedOperationException(this.getClass().toString() + "::copy()"));
    }

    public static interface Op {
	public void apply(Pipe pipe);

	public static class Composed implements Op {
	    private final Op[] ops;

	    public Composed(Op... ops) {
		int i, n;
		for(i = n = 0; i < ops.length; i++) {
		    if(ops[i] != null)
			n++;
		}
		if(n != i) {
		    Op[] td = new Op[n];
		    for(i = n = 0; i < ops.length; i++) {
			if(ops[i] != null)
			    td[n++] = ops[i];
		    }
		    this.ops = td;
		} else {
		    this.ops = ops;
		}
	    }

	    public void apply(Pipe pipe) {
		for(Op op : ops)
		    op.apply(pipe);
	    }

	    public boolean equals(Object o) {
		if(!(o instanceof Composed))
		    return(false);
		return(Arrays.equals(ops, ((Composed)o).ops));
	    }

	    public int hashCode() {
		return(Arrays.hashCode(ops));
	    }

	    public String toString() {
		return("#<composed " + Arrays.asList(ops) + ">");
	    }
	}

	public static Op compose(Op... ops) {
	    return(new Composed(ops));
	}
    }

    public default Pipe prep(Op op) {
	op.apply(this);
	return(this);
    }

    public static int hashCode(State[] states) {
	int h = 0x775e2d64;
	for(int i = 0; i < states.length; i++) {
	    if(states[i] != null)
		h = (h * 31) + states[i].hashCode();
	}
	return(h);
    }

    public static boolean equals(State[] as, State[] bs) {
	State[] a, b;
	if(as.length > bs.length) {
	    a = as; b = bs;
	} else {
	    b = as; a = bs;
	}
	int i = 0;
	for(; i < b.length; i++) {
	    if(!eq(a[i], b[i]))
		return(false);
	}
	for(; i < a.length; i++) {
	    if(a[i] != null)
		return(false);
	}
	return(true);
    }
}
