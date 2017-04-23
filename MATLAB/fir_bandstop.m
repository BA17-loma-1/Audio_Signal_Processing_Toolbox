% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: March 2017
% 
% FIR filter design: band stop

format compact; format short; clear; close all; clc;

fs = 48e3;                  % Sample rate (not relevant) [Hz]
Apass = 0.02;               % Rippel in Durchlassbereichen [dB]
fpass1 = 1000;              % Durchlassbereich links (pass band 1) [Hz]
fstop1 = 2000;              % Sperrbereich links (stop band 1) [Hz]
fpass2 = 4000;              % Durchlassbereich rechts (pass band 2) [Hz]
fstop2 = 3000;              % Sperrbereich rechts (stop band 2) [Hz]
Astop = 80;                 % min. Dämpfung im Sperrbereich [dB]

% Optimale Bestimmung des FIR-Filters
h = fdesign.bandstop(fpass1,fstop1,fstop2,fpass2,Apass,Astop,Apass,fs);
h_fir = design(h, 'fir', 'FilterStructure', 'dfsymfir', 'JointOptimization', true);
cost(h_fir)
b_fir = h_fir.numerator;
fprintf('DC gain\t\t\t\t\t\t\t: %d\n', sum(b_fir))


% Write filter spec and coefficients to text file
fd = fopen('output/b_fir_bandstop.txt', 'w+');
fprintf(fd, 'bandstop,order %d,fpass1 %5.3d,Apass1 %1.3f,fstop1 %5.3d,Astop1 %3.0d,fstop2 %5.3d,fpass2 %5.3d,Apass2 %1.3f\n', ...
    length(b_fir)-1, fpass1, Apass, fstop1, Astop, fstop2, fpass2, Apass);
fclose(fd);
dlmwrite('output/b_fir_bandstop.txt', b_fir, '-append', 'delimiter', ',', ...
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
