import { DEFAULT_IMAGE, IMAGE_OPTIONS } from "../constants/image-constants";
import { AVATAR_OPTIONS } from "../constants/avatar-constants";

export function getImageUrlByName(name: number): string {
  const nameStr = String(name);
  const image = IMAGE_OPTIONS.find(option => option.name === nameStr);
  return image ? image.url : DEFAULT_IMAGE;
}

export function getAvatarUrlById(id: number): string {
  const avatar = AVATAR_OPTIONS.find(option => option.id === id);
  return avatar ? avatar.url : AVATAR_OPTIONS[0].url;
}