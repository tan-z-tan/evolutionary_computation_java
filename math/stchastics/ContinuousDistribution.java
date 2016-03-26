package math.stchastics;

public interface ContinuousDistribution<V>
{
	public double density(V x);
	public V sample();
}
