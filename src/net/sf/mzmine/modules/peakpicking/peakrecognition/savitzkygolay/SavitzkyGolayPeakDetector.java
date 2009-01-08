/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.peakpicking.peakrecognition.savitzkygolay;

import java.util.Vector;
import java.util.logging.Logger;

import net.sf.mzmine.data.ChromatographicPeak;
import net.sf.mzmine.modules.peakpicking.peakrecognition.PeakResolver;
import net.sf.mzmine.modules.peakpicking.peakrecognition.ResolvedPeak;
import net.sf.mzmine.util.MathUtils;

/**
 * This class implements a peak builder using a match score to link MzPeaks in
 * the axis of retention time. Also uses Savitzky-Golay coefficients to
 * calculate the first and second derivative (smoothed) of raw data points
 * (intensity) that conforms each peak. The first derivative is used to
 * determine the peak's range, and the second derivative to determine the
 * intensity of the peak.
 * 
 */
public class SavitzkyGolayPeakDetector implements PeakResolver {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private double minimumPeakHeight, minimumPeakDuration, 
			derivativeThresholdLevel;

	/**
	 * Constructor of Savitzky-Golay Peak Builder
	 * 
	 * @param parameters
	 */
	public SavitzkyGolayPeakDetector(

	SavitzkyGolayPeakDetectorParameters parameters) {
		minimumPeakDuration = (Double) parameters
				.getParameterValue(SavitzkyGolayPeakDetectorParameters.minimumPeakDuration);
		minimumPeakHeight = (Double) parameters
				.getParameterValue(SavitzkyGolayPeakDetectorParameters.minimumPeakHeight);
		derivativeThresholdLevel = (Double) parameters
				.getParameterValue(SavitzkyGolayPeakDetectorParameters.derivativeThresholdLevel);


		// Create an instance of selected model class

	}

    /**
     */
    public ChromatographicPeak[] resolvePeaks(ChromatographicPeak chromatogram,
            int scanNumbers[], double retentionTimes[], double intensities[]) {
        
        Vector<ResolvedPeak> resolvedPeaks = new Vector<ResolvedPeak>();

		double maxIntensity = 0;
/*

		int[] scanNumbers = dataFile.getScanNumbers(1);
		double[] chromatoIntensities = new double[scanNumbers.length];
		double avgChromatoIntensities = 0;
		Arrays.sort(scanNumbers);

		for (int i = 0; i < scanNumbers.length; i++) {
			ConnectedMzPeak mzValue = chromatogram
					.getConnectedMzPeak(scanNumbers[i]);
			if (mzValue != null) {
				chromatoIntensities[i] = mzValue.getMzPeak().getIntensity();
			} else {
				chromatoIntensities[i] = 0;
			}

			if (chromatoIntensities[i] > maxIntensity)
				maxIntensity = chromatoIntensities[i];
			avgChromatoIntensities += chromatoIntensities[i];
		}

		avgChromatoIntensities /= scanNumbers.length;

		// If the current chromatogram has characteristics of background
		// return an empty array.
		if ((avgChromatoIntensities) > (maxIntensity * 0.5f))
			return detectedPeaks.toArray(new ChromatographicPeak[0]);

		double[] chromato2ndDerivative = SGDerivative.calculateDerivative(chromatoIntensities, false, 12);
		double noiseThreshold = calcDerivativeThreshold(chromato2ndDerivative);

		ChromatographicPeak[] chromatographicPeaks = SGPeaksSearch(dataFile,
				chromatogram, scanNumbers, chromato2ndDerivative, noiseThreshold);

		if (chromatographicPeaks.length != 0) {
			for (ChromatographicPeak p : chromatographicPeaks) {
				double pLength = p.getRawDataPointsRTRange().getSize();
				double pHeight = p.getHeight();
				if ((pLength >= minimumPeakDuration)
						&& (pHeight >= minimumPeakHeight)) {

					// Apply peak filling method
					if (fillingPeaks) {
						ChromatographicPeak shapeFilledPeak = peakModel.fillingPeak(p, new double[]{excessLevel, resolution});
						
						if (shapeFilledPeak == null)
							detectedPeaks.add(p);
						
						pLength = shapeFilledPeak.getRawDataPointsRTRange().getSize();
						pHeight = shapeFilledPeak.getHeight();
						
						if ((pLength >= minimumPeakDuration)
								&& (pHeight >= minimumPeakHeight)) {
							restPeaktoChromatogram(shapeFilledPeak, chromatogram);
							detectedPeaks.add(shapeFilledPeak);
						}
						else
							detectedPeaks.add(p);
							
					} else
						detectedPeaks.add(p);
				}
			}
		}
*/
		return resolvedPeaks.toArray(new ResolvedPeak[0]);

	}

	/**
	 * 
	 * 
	 * @param dataFile
	 * @param chromatogram
	 * @param scanNumbers
	 * @param derivativeOfIntensities
	 * @param noiseThreshold
	 * 
	 * @return ChromatographicPeak[]
	 */
    /*
	private ChromatographicPeak[] SGPeaksSearch(RawDataFile dataFile,
			Chromatogram chromatogram, int[] scanNumbers,
			double[] derivativeOfIntensities, double noiseThreshold) {

		boolean activeFirstPeak = false, activeSecondPeak = false, passThreshold = false;
		int crossZero = 0;

		Vector<ConnectedPeak> newPeaks = new Vector<ConnectedPeak>();
		Vector<ConnectedMzPeak> newMzPeaks = new Vector<ConnectedMzPeak>();
		Vector<ConnectedMzPeak> newOverlappedMzPeaks = new Vector<ConnectedMzPeak>();
		double maxDerivativeIntensity = 0;
		int indexMaxPoint = 0;

		for (int i = 1; i < derivativeOfIntensities.length; i++) {
			
			double absolute = derivativeOfIntensities[i]; //Math.abs(derivativeOfIntensities[i]);
			if (( absolute < maxDerivativeIntensity) && (crossZero == 2)) {
				maxDerivativeIntensity = absolute;
				indexMaxPoint = i;
			}
			
			//Changing sign and crossing zero
			if (((derivativeOfIntensities[i - 1] < 0.0f) && (derivativeOfIntensities[i] > 0.0f))
					|| ((derivativeOfIntensities[i - 1] > 0.0f) && (derivativeOfIntensities[i] < 0.0f))) {

				if ((derivativeOfIntensities[i - 1] < 0.0f)
						&& (derivativeOfIntensities[i] > 0.0f)) {
					if (crossZero == 2) {
						if (passThreshold) {
							activeSecondPeak = true;
						} else {
							newMzPeaks.clear();
							crossZero = 0;
							activeFirstPeak = false;
						}
					}

				}

				if (crossZero == 3) {
					activeFirstPeak = false;
				}

				// Always increment
				passThreshold = false;
				if ((activeFirstPeak) || (activeSecondPeak)) {
					crossZero++;
				}

			}

			if (Math.abs(derivativeOfIntensities[i]) > noiseThreshold) {
				passThreshold = true;
				if ((crossZero == 0) && (derivativeOfIntensities[i] > 0)) {
					// logger.finest("Prende primero " + crossZero);
					activeFirstPeak = true;
					crossZero++;
				}
			}

			//if (true){
			if ((activeFirstPeak)) {
				ConnectedMzPeak mzValue = chromatogram
						.getConnectedMzPeak(scanNumbers[i]);
				if (mzValue != null) {
					newMzPeaks.add(mzValue);
					
					 
				} else if (newMzPeaks.size() > 0) {
					activeFirstPeak = false;
					crossZero = 0;
				}

			}

			if (activeSecondPeak) {
				ConnectedMzPeak mzValue = chromatogram
						.getConnectedMzPeak(scanNumbers[i]);
				if (mzValue != null) {
					newOverlappedMzPeaks.add(mzValue);
					
					 
				}
			}

			if ((newMzPeaks.size() > 0) && (!activeFirstPeak)) {
				ConnectedPeak SGPeak = new ConnectedPeak(dataFile, newMzPeaks
						.elementAt(0));
				for (int j = 1; j < newMzPeaks.size(); j++) {
					SGPeak.addMzPeak(newMzPeaks.elementAt(j));
				}
				
				if (fillingPeaks) {
					
					ConnectedMzPeak mzValue = chromatogram
					.getConnectedMzPeak(scanNumbers[indexMaxPoint]);
					
					if (mzValue != null) {
						double height = mzValue.getMzPeak()
								.getIntensity();
						SGPeak.setHeight(height);

						double rt = mzValue.getScan()
								.getRetentionTime();
						SGPeak.setRT(rt);
					}

				indexMaxPoint = 0;
				maxDerivativeIntensity = 0;
				}
				
				
				
				newMzPeaks.clear();
				newPeaks.add(SGPeak);

				if ((newOverlappedMzPeaks.size() > 0) && (activeSecondPeak)) {
					for (ConnectedMzPeak p : newOverlappedMzPeaks)
						newMzPeaks.add(p);
					activeSecondPeak = false;
					activeFirstPeak = true;
					crossZero = 2;
					newOverlappedMzPeaks.clear();
					passThreshold = false;

				}
			}

		}

		if (newMzPeaks.size() > 0) {
			ConnectedPeak SGPeak = new ConnectedPeak(dataFile, newMzPeaks
					.elementAt(0));
			for (int j = 1; j < newMzPeaks.size(); j++) {
				SGPeak.addMzPeak(newMzPeaks.elementAt(j));
			}
			newMzPeaks.clear();
			newPeaks.add(SGPeak);
		}

		return newPeaks.toArray(new ConnectedPeak[0]);
	}

	

	/**
	 * 
	 * @param chromatoIntensities
	 * @return noiseThresholdLevel
	 */
	private double calcDerivativeThreshold(double[] derivativeIntensities) {

		double[] intensities = new double[derivativeIntensities.length];
		for (int i = 0; i < derivativeIntensities.length; i++) {
			intensities[i] = (double) Math.abs(derivativeIntensities[i]);
		}

		return MathUtils.calcQuantile(intensities, derivativeThresholdLevel);
	}
	
	
	
	/**
	 * @param shapeFilledPeak
	 * @param chromatogram
	 */
    /*
	public void restPeaktoChromatogram(ChromatographicPeak shapeFilledPeak, Chromatogram chromatogram){
		
		ConnectedMzPeak[] listMzPeaks = ((ConnectedPeak) shapeFilledPeak)
		.getAllMzPeaks();
		int scanNumber = 0;
		double filledIntensity = 0, originalIntensity = 0, restedIntensity;
		ConnectedMzPeak mzValue = null;
		
		for (ConnectedMzPeak mzPeak: listMzPeaks){
			scanNumber = mzPeak.getScan().getScanNumber();
			filledIntensity = mzPeak.getMzPeak().getIntensity();
			mzValue = chromatogram.getConnectedMzPeak(scanNumber);
			if (mzValue != null){
				originalIntensity = mzValue.getMzPeak().getIntensity();
				restedIntensity = originalIntensity - filledIntensity;
				if (restedIntensity < 0)
					restedIntensity = 0;
				((SimpleMzPeak) mzValue.getMzPeak()).setIntensity(restedIntensity);
			}
			
		}
	}
    */

}