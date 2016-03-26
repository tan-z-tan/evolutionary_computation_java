package geneticProgramming;

public class DefaultGpIndividual extends GpIndividual
{
	@Override
	public Object evaluate()
	{
		return this.getRootNode().evaluate(this);
	}
}
