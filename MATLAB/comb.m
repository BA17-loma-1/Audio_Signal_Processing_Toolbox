% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: April 2017
% 
% Comb filter design
% From www.johnloomis.org/ece561/notes/zeropole/comb.html

function[] = comb()

format compact; format shortE; clear; close all; clc;

[b,~] = combfilter(0.995, 4);

% Write coefficients to text file
dlmwrite('output/comb.txt', b, 'delimiter', ',', 'precision', '%1.12f');

figure(1);
stem(b, 'filled'), grid minor;
title('Impulse response (coefficients of the comb filter)');
xlabel('Samples i');
ylabel('Amplitude b[i]');


    % Example: [b,a] = comb(0.95, 16)
    function[b,a] = combfilter(r,L)
        b = [1 zeros(1, L-1) -r^L];
        a = [1 zeros(1, L-1)];
    end

end