% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: March 2017
% 
% FIR filter design: band pass

format compact; format short; clear; close all; clc;

fs = 48e3;                  % Sample rate (not relevant) [Hz]
Apass = 0.02;               % Rippel im Durchlassbereich [dB]
fpass1 = 2.5e3;             % Durchlassbereich links (pass band 1) [Hz]
fstop1 = 2e3;               % Sperrbereich links (stop band1 ) [Hz]
fpass2 = 3e3;               % Durchlassbereich rechts (pass band 2) [Hz]
fstop2 = 3.5e3;             % Sperrbereich rechts (stop band 2) [Hz]
Astop = 90;                 % min. Dämpfung in Sperrbereichen [dB]

% Optimale Bestimmung des FIR-Filters
h = fdesign.bandpass(fstop1,fpass1,fpass2,fstop2,Astop,Apass,Astop,fs);
h_fir = design(h, 'equiripple');%'fir', 'FilterStructure', 'dfsymfir', 'JointOptimization', true);
cost(h_fir)
b_fir = h_fir.numerator;
fprintf('DC gain\t\t\t\t\t\t\t: %d\n', sum(b_fir))


% Write filter spec and coefficients to text file
fd = fopen('output/b_fir_bandpass.txt', 'w+');
fprintf(fd, 'bandpass,order %d,fstop1 %5.3d,Astop1 %3.0d,fpass1 %5.3d,Apass1 %1.3f,fpass2 %5.3d,fstop2 %5.3d,Astop2 %3.0d\n', ...
    length(b_fir)-1, fstop1, Astop, fpass1, Apass, fpass2, fstop2, Astop);
fclose(fd);
dlmwrite('output/b_fir_bandpass.txt', b_fir, '-append', 'delimiter', ',', ...
    'precision', '%1.12f');


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
