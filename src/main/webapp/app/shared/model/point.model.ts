export interface IPoint {
  id?: string;
  title?: string;
  description?: string | null;
  activated?: boolean;
}

export const defaultValue: Readonly<IPoint> = {
  activated: false,
};
