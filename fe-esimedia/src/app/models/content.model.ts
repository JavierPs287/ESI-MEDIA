export interface Content {
  title: string;
  description?: string;
  type: "AUDIO" | "VIDEO";
  tags?: string[];
  duration?: number;
  vip?: boolean;
  visible?: boolean;
  visibilityChangeDate?: Date;
  visibilityDeadline?: Date;
  minAge?: number;
  imageId: number;
  creador: string;
  rating?: number;
  views?: number;
  urlId: string;
}