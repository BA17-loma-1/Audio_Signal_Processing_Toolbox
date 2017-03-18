% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: March 2017
% 
% FIR filter design: bandpass

format compact; format long; clear; close all; clc;

q = 15;                     % Quantization bits
fs = 48e3;                  % Sample rate (not relevant) [Hz]

Apass = 0.1737;             % Rippel im  Durchlassbereich +/- 0.087 dB
fpass1 = 3e3;               % Durchlassbereich links (passband), Matrize [Hz]
fstop1 = 2e3;               % Sperrbereich links (stopband), Stempel [Hz]
fpass2 = 5e3;               % Durchlassbereich rechts (passband), Matrize [Hz]
fstop2 = 6e3;               % Sperrbereich rechts (stopband), Stempel [Hz]
Astop = 80;                 % min. Dämpfung in Sperrbereichen [dB]

% Optimale Bestimmung des FIR-Tiefpassfilters
h = fdesign.bandpass(fstop1,fpass1,fpass2,fstop2,Astop,Apass,Astop,fs);
h_fir = design(h, 'fir', 'FilterStructure', 'dfsymfir', 'JointOptimization', true);
cost(h_fir)
b_fir = h_fir.numerator;

% Save the filter coefficients in signed Q15 format
b_fir_q = fix(b_fir * 2^q);
dlmwrite('output/b_fir_bandpass.txt', b_fir);


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
