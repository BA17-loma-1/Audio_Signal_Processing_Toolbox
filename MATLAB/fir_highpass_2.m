% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: May 2017
% 
% FIR filter design: high pass

function [b_fir] = fir_highpass_2()

fs = 48e3;                  % Sample rate (not relevant) [Hz]
fpass = 2500;               % Durchlassbereich (pass band) [Hz]
Apass = 0.02;               % Rippel im Durchlassbereich [dB]
fstop = 1000;               % Sperrbereich (stop band) [Hz]
Astop = 60;                 % min. Dämpfung im Sperrbereich [dB]

% Optimale Bestimmung des FIR-Filters
h = fdesign.highpass(fstop, fpass, Astop, Apass, fs);
h_fir = design(h, 'fir', 'FilterStructure', 'dfsymfir', 'JointOptimization', true);
cost(h_fir)
b_fir = h_fir.numerator;
fprintf('DC gain\t\t\t\t\t\t\t: %d\n', sum(b_fir))


% Write filter spec and coefficients to text file
fd = fopen('output/b_fir_highpass2.txt', 'w+');
fprintf(fd, 'highpass,order %d,fstop1 %5.3d,Astop1 %3.0d,fpass1 %5.3d,Apass1 %1.3f\n', ...
    length(b_fir)-1, fstop, Astop, fpass, Apass);
fclose(fd);
dlmwrite('output/b_fir_highpass2.txt', b_fir, '-append', 'delimiter', ',', ...
    'precision', '%1.12f');


figure(1);
subplot(1,2,1), stem(b_fir, 'filled'), grid minor;
title('Impulse response (coefficients of the FIR filter)');
xlabel('Samples i');
ylabel('Amplitude b[i]');
xlim([1 length(b_fir + 1)]);

[H,W] = freqz(b_fir, 1, 2^13);
subplot(1,2,2), plot(W/2/pi*fs, 20*log10(abs(H))), grid minor;
title('Frequency response of the FIR filter');
xlabel('Frequency [Hz]');
ylabel('Magnitude [dB]');

fvtool(b_fir)

end