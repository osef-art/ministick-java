from PIL import Image

def color_hue(r, g, b, hue= 1):
  return (min(int(r*hue), 255), min(int(g*hue), 255), min(int(b*hue), 255), 255)

def color_set(code, rgb, eyes= None):
  dict = {}
  (r,g,b) = rgb

  dict['code'] = str(code)
  dict['principal'] = color_hue(r, g, b)
  dict['shade'] = color_hue(r, g, b, 9/10)
  dict['shade2'] = color_hue(r, g, b, 9/10)
  dict['eyes'] = color_hue(r, g, b, 1/2) if (eyes is None) else eyes + (255,)
  
  rf, gf, bf, rc, gc, bc = r+40, g+40, b+40, r+30, g+30, b+10
  
  dict['charge-principal'] = color_hue(rc, gc, bc)
  dict['charge-shade'] = color_hue(rc, gc, bc, 9/10)
  dict['charge-eyes'] = color_hue(rc, gc, bc, 1/2) if (eyes is None) else eyes + (255,)

  dict['flash-principal'] = color_hue(rf, gf, bf)
  dict['flash-shade'] = color_hue(rf, gf, bf, 9/10)
  dict['flash-eyes'] = color_hue(rf, gf, bf, 1/2) if (eyes is None) else eyes + (255,)
  return dict

directories, afters, colors = [], [], []

"""
before = {'principal': (152, 152, 152, 255), 'shade': (144, 144, 144, 255), 'eyes': (64, 64, 64, 255), 
          'charge-principal': (216, 192, 173, 255), 'charge-shade': (189, 168, 152, 255),
          'flash-principal': (186, 186, 186, 255),  'shade2': (135, 135, 135, 255),
          'code': 1}
"""       
sets = [
  color_set("_dash", (100, 200, 200)),
  color_set("_double", (37, 37, 37), (175, 175, 175)),
  color_set("_double", (37, 37, 37), (150, 150, 150)),
  color_set(1, (152, 152, 152)),
  color_set(2, (100, 100, 100)),
  color_set(3, (100, 90, 200)),
  color_set(4, (150, 130, 200)),
  color_set(5, (150, 250, 150), (25, 150, 25))
]   

print(sets[-1])
"""
before = sets[2]

afters.append( sets[1] )

directories.append({ "nb_frames": 2, "action": "charge_upp" })
directories.append({ "nb_frames": 4, "action": "dashpunch" })
directories.append({ "nb_frames": 8, "action": "uppercut" })
"""
directories.append({ "nb_frames": 2, "action": "charging" })
directories.append({ "nb_frames": 2, "action": "charge2" })
directories.append({ "nb_frames": 4, "action": "looping" })
directories.append({ "nb_frames": 4, "action": "punch2" })
directories.append({ "nb_frames": 2, "action": "stand" })
directories.append({ "nb_frames": 2, "action": "squat" })
directories.append({ "nb_frames": 2, "action": "float" })
directories.append({ "nb_frames": 4, "action": "getup" })
directories.append({ "nb_frames": 4, "action": "punch" })
directories.append({ "nb_frames": 8, "action": "death" })
directories.append({ "nb_frames": 2, "action": "hurt" })
directories.append({ "nb_frames": 2, "action": "walk" })
directories.append({ "nb_frames": 2, "action": "fall" })
directories.append({ "nb_frames": 4, "action": "jump" })

def get_names(directory):
  names = []
  for frame in range(0, directory["nb_frames"]):
    for direction in ["l", "r"]:
      names.append("enemy" + str(before['code']) + "/" +  directory["action"] + "_" + direction + str(frame) + ".png")
  return names
  
def new_name(name, after):
  new_name = list(name)
  new_name[5] = after['code']
  while new_name[6] != '/':
    new_name.pop(6)
  return "".join(new_name)

def add_color(color):
  if color not in colors:
    colors.append(color)

def rgb_to_hexa(c):
  return '#%02x%02x%02x' % (c[0], c[1], c[2])

def hex_to_rgb(value):
  value = value.lstrip('#')
  lv = len(value.lstrip('#'))
  return tuple(int(value[i:i + lv // 3], 16) for i in range(0, lv, lv // 3)) + (255,)


if __name__ == "__main__":
  for after in afters:
    for directory in directories:
      names = get_names(directory)
      for num, name in enumerate(names):
        im = Image.open(name) 
        pix = im.load()
        
        for i in range(256):
          for j in range(256):
            add_color(pix[i,j])
            for key in before:
              if (key != 'code' and before[key] == pix[i,j]):
                pix[i,j] = after[key]

        file_name = new_name(name, after)
        im.save(file_name)
        print(file_name + ":", "[", "{:.1f}".format((num+1)*100/len(names)), "% ", "redessinnés... ]       ", end= "\r")
print(75 * ' ', end= "\r")
print('Done !')