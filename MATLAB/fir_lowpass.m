% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: March 2017
% 
% FIR filter design: lowpass

format compact; format short; clear; close all; clc;

q = 15;                     % Quantization bits
fs = 48e3;                  % Sample rate (not relevant) [Hz]

fpass = 1e3;                % Durchlassbereich (passband), Matrize [Hz]
Apass = 0.1737;             % Rippel im  Durchlassbereich +/- 0.087 dB
fstop = 2e3;                % Sperrbereich (stopband), Stempel [Hz]
Astop = 80;                 % min. Dämpfung im Sperrbereich [dB]

% Optimale Bestimmung des FIR-Tiefpassfilters
h = fdesign.lowpass(fpass, fstop, Apass, Astop, fs);
h_fir = design(h, 'fir', 'FilterStructure', 'dfsymfir', 'JointOptimization', true);
cost(h_fir)
b_fir = h_fir.numerator;

% Save the filter coefficients in signed Q15 format
b_fir_q = fix(b_fir * 2^q);
dlmwrite('output/b_fir_lowpass.txt', b_fir, 'precision', '%1.12f');


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
