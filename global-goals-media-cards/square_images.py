from PIL import Image
import os
import glob
import shutil

size = 220, 220
white_square = Image.open("white_square.png").crop([0, 0, size[0], size[0]]).convert('RGB')

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

def make_white_square():
    return white_square.copy()

def make_thumbnail(image, size=200):
    w = make_white_square()
    image.thumbnail([size, size])
    w.paste(image, (10, 10))
    return w

# def make_thumbnail(image, size=54*6):
#     # w = make_white_square()
#     image.thumbnail([size, size])
#     # w.paste(image, (5, 5))
#     return image

def main():
    input_files = sorted(glob.glob('../public/images/**/*TARGET*_SQUARE.png', recursive=True))
    # input_files = sorted(glob.glob('../public/images/**/*TheGlobalGoals*Color*.png', recursive=True))
    # input_files = sorted(glob.glob('../public/images/**/*TheGlobalGoals*Color*_THUMBNAIL.png', recursive=True))

    print(len(input_files))
    x_0 = 50
    y_0 = 370
    h = 1400
    for image in input_files:
        # os.remove(image)
        image_obj = Image.open(image)
        thumbnail = make_thumbnail(image_obj)
        thumbnail.save(os.path.splitext(image)[0] + "_THUMBNAIL.png")

if __name__ == '__main__':
    main()
