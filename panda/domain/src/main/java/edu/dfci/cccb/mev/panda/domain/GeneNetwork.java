package edu.dfci.cccb.mev.panda.domain;

import static java.util.Arrays.asList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public class GeneNetwork {

	public Iterable<GeneInteraction> findPairsWithStrengthGreaterThan(double value) {
		@Accessors(fluent = true)
		@RequiredArgsConstructor
		class GP implements GeneInteraction {
			public final @Getter
			String source;
			public final @Getter
			String target;
			public final @Getter
			double weight;
			public final @Getter
			double strength;
		}

		return asList((GeneInteraction) new GP("SourceGene", "TargetGene", 0.3, 0.4),
				new GP("s1", "t1", .3, .4));
	}
}