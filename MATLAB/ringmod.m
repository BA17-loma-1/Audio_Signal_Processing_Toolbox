% BAIT17 - Audio Signal Processing Toolbox for Android
% author: georgrem, stockan1
% version: May 2017
% 
% Audio effect: Ring modulator

function [y] = ringmod(x, fs, modFreq)

y = zeros(1, length(x));
index = 0;

for i=1:length(x)
    y(i) = x(i) * sin(2*pi*modFreq * (index/fs));
    index = index + 1;
    if (index == fs)
        index = 0;
    end
end

end