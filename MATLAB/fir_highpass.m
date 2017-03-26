% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: March 2017
% 
% FIR filter design: high pass

format compact; format long; clear; close all; clc;

fs = 48e3;                  % Sample rate (not relevant) [Hz]
fpass = 3e3;                % Durchlassbereich (passband), Matrize [Hz]
Apass = 0.1737;             % Rippel im  Durchlassbereich +/- 0.087 dB
fstop = 2e3;                % Sperrbereich (stopband), Stempel [Hz]
Astop = 60;                 % min. Dämpfung im Sperrbereich [dB]

% Optimale Bestimmung des FIR-Tiefpassfilters
h = fdesign.highpass(fstop, fpass, Astop, Apass, fs);
h_fir = design(h, 'fir', 'FilterStructure', 'dfsymfir', 'JointOptimization', true);
cost(h_fir)
b_fir = h_fir.numerator;

% Save the filter coefficients
dlmwrite('output/b_fir_highpass.txt', b_fir, 'precision', '%1.12f');


figure(1);
stem(b_fir, 'filled'), grid minor;
title('Impulse response (coefficients of the FIR filter)');
xlabel('Samples i');
ylabel('Amplitude b[i]');

figure(2);
[H,W] = freqz(b_fir, 1, 2^13);
plot(W/2/pi*fs, 20*log10(abs(H))), grid minor;
title('Frequency response of the FIR filter');
xlabel('Frequency [Hz]');
ylabel('Magnitude [dB]');

fvtool(b_fir)
