package multeval;

import java.util.ArrayList;
import java.util.List;

import multeval.metrics.SuffStats;

public class SuffStatManager {

	// indexices iSys, iOpt, iMetric, iHyp; inner array: various suff stats for
	// a particular metric
	private final List<List<List<List<SuffStats<?>>>>> statsBySys;
	private final int numMetrics;
	private final int numOpt;
	private final int numHyp;

	private static final SuffStats<?> DUMMY = null;

	public SuffStatManager(int numMetrics, int numSys, int numOpt, int numHyp) {
		this.numMetrics = numMetrics;
		this.numOpt = numOpt;
		this.numHyp = numHyp;
		this.statsBySys = new ArrayList<List<List<List<SuffStats<?>>>>>(numSys);
	}

	public void saveStats(int iMetric, int iSys, int iOpt, int iHyp, SuffStats<?> stats) {
		// first, expand as necessary
		// TODO: Use more intelligent list type that allows batch grow
		// operations
		while (statsBySys.size() <= iSys) {
			statsBySys.add(new ArrayList<List<List<SuffStats<?>>>>(numOpt));
		}
		List<List<List<SuffStats<?>>>> statsByOpt = statsBySys.get(iSys);
		while (statsByOpt.size() <= iOpt) {
			statsByOpt.add(new ArrayList<List<SuffStats<?>>>(numMetrics));
		}
		List<List<SuffStats<?>>> statsByMetric = statsByOpt.get(iOpt);
		while (statsByMetric.size() <= iMetric) {
			statsByMetric.add(new ArrayList<SuffStats<?>>(numHyp));
		}
		List<SuffStats<?>> statsByHyp = statsByMetric.get(iMetric);
		while (statsByHyp.size() <= iHyp) {
			statsByHyp.add(DUMMY);
		}
		statsByHyp.set(iHyp, stats);
	}
	
	// inner array: various suff stats for a particular metric
	public SuffStats<?> getStats(int iMetric, int iSys, int iOpt, int iHyp) {
		// TODO: More informative error messages w/ bounds checking
		return getStats(iMetric, iSys, iOpt).get(iHyp);
	}

	// list index: iHyp; inner array: various suff stats for a particular metric
	public List<SuffStats<?>> getStats(int iMetric, int iSys, int iOpt) {
		// TODO: More informative error messages w/ bounds checking
		return getStats(iSys, iOpt).get(iMetric);
	}

	// indices: iMetric, iHyp
	public List<List<SuffStats<?>>> getStats(int iSys, int iOpt) {
		// TODO: More informative error messages w/ bounds checking
		return statsBySys.get(iSys).get(iOpt);
	}

	// appends all optimization runs together
	// indices: iMetric, iHyp; inner array: various suff stats for a particular
	// metric
	public List<List<SuffStats<?>>> getStatsAllOptForSys(int iSys) {

		List<List<SuffStats<?>>> resultByMetric = new ArrayList<List<SuffStats<?>>>(numMetrics);
		
		List<List<List<SuffStats<?>>>> statsByOpt = statsBySys.get(iSys);
		
		for(int iMetric=0; iMetric<numMetrics; iMetric++) {
			ArrayList<SuffStats<?>> resultByHyp = new ArrayList<SuffStats<?>>(numHyp * numOpt);
			resultByMetric.add(resultByHyp);
			for (int iOpt = 0; iOpt < numOpt; iOpt++) {
				List<List<SuffStats<?>>> statsByMetric = statsByOpt.get(iOpt);
				List<SuffStats<?>> statsByHyp = statsByMetric.get(iMetric);
				resultByHyp.addAll(statsByHyp); 
			}
		}
		return resultByMetric;
	}
}
