import { DEFAULT_IMAGE, IMAGE_OPTIONS } from "../constants/image-constants";

export function getImageUrlByName(name: number): string {
  const nameStr = String(name);
  const image = IMAGE_OPTIONS.find(option => option.name === nameStr);
  return image ? image.url : DEFAULT_IMAGE;
}