__author__ = 'abarbi'

from scipy import misc
from skimage import color
from skimage import measure
import matplotlib.pyplot as plt
fimg = misc.imread("num0.jpg")
gimg = color.colorconv.rgb2grey(fimg)
contours = measure.find_contours(gimg, 0.8)
for n, contour in enumerate(contours):
    plt.plot(contour[:, 1], contour[:, 0], linewidth=2)