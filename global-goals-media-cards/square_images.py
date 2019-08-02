from PIL import Image
import os
import glob

def crop(image_path, coords, saved_location):
    """
    @param image_path: The path to the image to edit
    @param coords: A tuple of x/y coordinates (x1, y1, x2, y2)
    @param saved_location: Path to save the cropped image
    """
    image_obj = Image.open(image_path)
    cropped_image = image_obj.crop(coords)
    cropped_image.save(saved_location)
    # cropped_image.show()

def main():
    input_files = sorted(glob.glob('../public/images/**/*TARGET*.png', recursive=True))
    x_0 = 50
    y_0 = 370
    h = 1400
    for image in input_files:
        crop(image, (x_0, y_0, x_0+h, y_0+h), os.path.splitext(image)[0] + "_SQUARE.png")




if __name__ == '__main__':
    main()
