/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.codefx.lab.arraylist;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 2, time = 2)
@Fork(1)
@State(Scope.Thread)
public class RemoveBenchmark {

	@Param({ "10_000", "100_000", "1_000_000" })
	private String arrayLength = "1_000";

	private int[] removeAts;

//	@Param({ "0.0", "0.1", "0.2", "0.4" })
//	private float removeQuota = 0;
//
//	@Setup(Level.Iteration)
//	public void createRemovalIndices() {
//		Random r = new Random();
//		int length = Integer.valueOf(arrayLength.replace("_", ""));
//		int removalLength = (int) (length * removeQuota);
//
//		removeAts = new int[removalLength];
//		for (int i = 0; i < removalLength; i++) {
//			removeAts[i] = r.nextInt(length);
//		}
//	}

	@Param({ "0", "10", "100", "1_000" })
	private String removals = "0";

	@Setup(Level.Iteration)
	public void createRemovalIndices() {
		Random r = new Random();
		int length = Integer.valueOf(arrayLength.replace("_", ""));
		int removals = Integer.valueOf(this.removals.replace("_", ""));

		removeAts = new int[removals];
		for (int i = 0; i < removals; i++) {
			removeAts[i] = r.nextInt(length);
		}
	}

	@Benchmark
	public void baseline(Blackhole bh) {
		List<Integer> list = createArrayList();
		bh.consume(list);
	}

	@Benchmark
	public void iterativeAt(Blackhole bh) {
		List<Integer> list = createArrayList();
		List<Integer> removed = IterativeAtRemover.remove(list, removeAts);
		bh.consume(removed);
	}

	@Benchmark
	public void listIterator(Blackhole bh) {
		List<Integer> list = createLinkedList();
		List<Integer> removed = ListIteratorRemover.remove(list, removeAts);
		bh.consume(removed);
	}

	@Benchmark
	public void copyArrayRemove(Blackhole bh) {
		List<Integer> list = createArrayList();
		List<Integer> removed = ArrayCopyRemover.remove(list, removeAts);
		bh.consume(removed);
	}

	private List<Integer> createArrayList() {
		int length = Integer.valueOf(arrayLength.replace("_", ""));
		return fill(new ArrayList<>(length), length);
	}

	private List<Integer> createLinkedList() {
		int length = Integer.valueOf(arrayLength.replace("_", ""));
		return fill(new LinkedList<>(), length);
	}

	private static List<Integer> fill(List<Integer> list, int length) {
		for (int i = 0; i < length; i++) {
			list.add(-i);
		}
		return list;
	}

}
