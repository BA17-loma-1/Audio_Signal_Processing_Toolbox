% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: March 2017
% 
% FIR filter design: band pass

function [b_fir] = fir_bandpass()

clear; clc;

fs = 48e3;                  % Sample rate (not relevant) [Hz]
Apass = 0.01;               % Rippel im Durchlassbereich [dB]
fpass1 = 1500;              % Durchlassbereich links (pass band 1) [Hz]
fstop1 = 500;               % Sperrbereich links (stop band1 ) [Hz]
fpass2 = 2500;              % Durchlassbereich rechts (pass band 2) [Hz]
fstop2 = 3250;              % Sperrbereich rechts (stop band 2) [Hz]
Astop = 80;                 % min. Dämpfung in Sperrbereichen [dB]

% Optimale Bestimmung des FIR-Filters
h = fdesign.bandpass(fstop1,fpass1,fpass2,fstop2,Astop,Apass,Astop,fs);
h_fir = design(h, 'fir', 'FilterStructure', 'dfsymfir', 'JointOptimization', true);
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
[H,W] = freqz(b_fir, 1, 2^13);
subplot(1,2,1), plot(W/2/pi*fs, 20*log10(abs(H))), grid minor;
title('Frequency response');
xlabel('Frequency [Hz]');
ylabel('Magnitude [dB]');

subplot(1,2,2), stem(b_fir, 'filled'), grid minor;
title('Impulse response');
xlabel('Samples i');
ylabel('Amplitude b[i]');
xlim([1 length(b_fir + 1)]);

fvtool(b_fir)

end