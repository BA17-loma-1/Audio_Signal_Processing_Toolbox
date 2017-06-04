% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: March 2017
% 
% FIR filter design: low pass

function [b_fir] = fir_lowpass()

clear; clc;

fs = 48e3;                  % Sample rate (not relevant) [Hz]
fpass = 250;                % Durchlassbereich (pass band), Matrize [Hz]
Apass = 0.01;               % Rippel im Durchlassbereich [dB]
fstop = 750;                % Sperrbereich (stop band), Stempel [Hz]
Astop = 80;                 % min. Dämpfung im Sperrbereich [dB]

% Optimale Bestimmung des FIR-Filters
h = fdesign.lowpass(fpass, fstop, Apass, Astop, fs);
h_fir = design(h, 'fir', 'FilterStructure', 'dfsymfir', 'JointOptimization', true);
cost(h_fir)
b_fir = h_fir.numerator;
fprintf('DC gain\t\t\t\t\t\t\t: %d\n', sum(b_fir))


% Write filter spec and coefficients to text file
fd = fopen('output/b_fir_lowpass.txt', 'w+');
fprintf(fd, 'lowpass,order %d,fpass1 %5.3d,Apass1 %1.3f,fstop1 %5.3d,Astop1 %3.0d\n', ...
    length(b_fir)-1, fpass, Apass, fstop, Astop);
fclose(fd);
dlmwrite('output/b_fir_lowpass.txt', b_fir, '-append', 'delimiter', ',', ...
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