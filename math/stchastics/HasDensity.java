package math.stchastics;


/**
 * Interface that signals that a distribution can compute the probability density function
 * for a particular point.
 * @param <P> the type of the point at which density is to be computed, this
 * may be for example <code>Double</code>
 * @version $Revision: 1.1 $ $Date: 2009/05/15 15:16:07 $
 */
public interface HasDensity<P> {

    /**
     * Compute the probability density function.
     * @param x point for which the probability density is requested
     * @return probability density at point x
     * @throws MathException if probability density cannot be computed at specifed point
     */
    double density(P x);

}
